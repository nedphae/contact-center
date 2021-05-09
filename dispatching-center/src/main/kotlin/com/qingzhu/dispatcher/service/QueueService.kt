package com.qingzhu.dispatcher.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.component.MessageService
import com.qingzhu.dispatcher.domain.dto.ConversationViewDto
import com.qingzhu.dispatcher.domain.entity.UserQueue
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Service
class QueueService(
    private val messageService: MessageService,
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val assignmentService: AssignmentService,
) {
    private val hashOps: ReactiveHashOperations<String, Long, String> by lazy { redisTemplate.opsForHash() }

    fun removeUser(organizationId: Int, userId: Long): Flux<ResponseEntity<Unit>> {
        val customerDispatcherDto = messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
        val atomicInteger = AtomicInteger(0)
        // fuck vips, all beings are equal
        // val vipAtomicInteger = AtomicInteger(0)
        val atomicBoolean = AtomicBoolean(false)
        return customerDispatcherDto
            .flatMapMany { cdd ->
                hashOps.remove("queue:${cdd.shuntId}", cdd.userId)
                    .doOnNext { if (it == 0L) atomicBoolean.set(true) }
                    .flatMapMany {
                        hashOps.scan("queue:${cdd.shuntId}")
                    }
            }
            .map { JsonUtils.fromJson<UserQueue>(it.value) }
            .sort { o1, o2 -> (o1.inQueueTime - o2.inQueueTime).toInt() }
            .flatMap { uq ->
                var queue = atomicInteger.getAndIncrement()
                queue = if (atomicBoolean.get()) queue - 1 else queue
                if (queue == -1) {
                    // TODO: 获取分布式锁 or 客户端拒绝？
                    assignmentService.assignmentStaff(uq.organizationId, uq.userId, true)
                        .filter { v -> v.staffId != null }
                        .flatMap {
                            // 发送系统消息，通知分配成功客服
                            messageService.send(
                                MessageDto(
                                    Message(
                                        organizationId = uq.organizationId,
                                        conversationId = -1,
                                        to = uq.userId,
                                        type = CreatorType.CUSTOMER,
                                        creatorType = CreatorType.SYS,
                                        content = Content(
                                            MessageType.SYS,
                                            sysCode = SysCode.ASSIGN,
                                            textContent = Content.TextContent(it.toJson())
                                        )
                                    )
                                ).toMono()
                            )
                        }
                        .doOnNext {
                            hashOps.remove("queue:${uq.shuntId}", uq.userId).subscribe()
                        }
                } else {
                    messageService.send(
                        MessageDto(
                            Message(
                                organizationId = uq.organizationId,
                                conversationId = -1,
                                to = uq.userId,
                                type = CreatorType.CUSTOMER,
                                creatorType = CreatorType.SYS,
                                content = Content(
                                    MessageType.SYS,
                                    sysCode = SysCode.UPDATE_QUEUE,
                                    textContent = Content.TextContent(
                                        ConversationViewDto(
                                            uq.organizationId,
                                            uq.userId,
                                            queue.toLong()
                                        ).toJson()
                                    )
                                )
                            )
                        ).toMono()
                    )
                }
            }
    }
}