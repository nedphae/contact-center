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
import com.qingzhu.dispatcher.domain.dto.StaffStatusDto
import com.qingzhu.dispatcher.domain.entity.UserQueue
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Service
class QueueService(
    private val messageService: MessageService,
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val assignmentService: AssignmentService,
) {
    private val hashOps: ReactiveHashOperations<String, String, String> by lazy { redisTemplate.opsForHash() }

    fun assignmentFromQueue(staffStatusDto: StaffStatusDto): Mono<Void> {
        val maxServiceCount = AtomicInteger(staffStatusDto.maxServiceCount)
        val atomicInteger = AtomicInteger(0)
        return staffStatusDto.toMono()
            .flatMapIterable { it.priorityOfShunt.entries }
            .sort { o1, o2 -> o1.value - o2.value }
            .map { it.key }
            .flatMap {
                hashOps.scan("queue:${it}")
            }
            .map { JsonUtils.fromJson<UserQueue>(it.value) }
            .sort { o1, o2 -> (o1.inQueueTime - o2.inQueueTime).toInt() }
            .flatMap {
                val nowService = maxServiceCount.getAndDecrement()
                val queue = if (nowService > 0) -1 else atomicInteger.getAndIncrement()
                assignmentByQueueNum(queue, it.organizationId, it.shuntId, it.userId)
            }
            .then()
    }

    private fun assignmentByQueueNum(queue: Int, organizationId: Int, shuntId: Long, userId: Long): Mono<ResponseEntity<Void>> {
        return if (queue == -1) {
            // TODO: 获取分布式锁 or 客户端拒绝？
            assignmentService.assignmentStaff(organizationId, userId, true)
                .filter { v -> v.staffId != null }
                .flatMap {
                    // 发送系统消息，通知分配成功客服
                    messageService.send(
                        MessageDto(
                            Message(
                                organizationId = organizationId,
                                conversationId = -1,
                                to = userId,
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
                    // 分配成功 删除排队客户
                    hashOps.remove("queue:${shuntId}", userId.toString()).subscribe()
                }
        } else {
            messageService.send(
                MessageDto(
                    Message(
                        organizationId = organizationId,
                        conversationId = -1,
                        to = userId,
                        type = CreatorType.CUSTOMER,
                        creatorType = CreatorType.SYS,
                        content = Content(
                            MessageType.SYS,
                            sysCode = SysCode.UPDATE_QUEUE,
                            textContent = Content.TextContent(
                                ConversationViewDto(
                                    organizationId,
                                    userId,
                                    shuntId,
                                    queue.toLong()
                                ).toJson()
                            )
                        )
                    )
                ).toMono()
            )
        }
    }

    fun removeUser(organizationId: Int, userId: Long): Mono<Void> {
        val customerDispatcherDto = messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
        val atomicInteger = AtomicInteger(0)
        // fuck vips, all beings are equal
        // val vipAtomicInteger = AtomicInteger(0)
        val atomicBoolean = AtomicBoolean(false)
        return customerDispatcherDto
            .flatMapMany { cdd ->
                hashOps.remove("queue:${cdd.shuntId}", cdd.userId.toString())
                    .doOnNext { if (it == 0L) atomicBoolean.set(true) }
                    .flatMapMany {
                        hashOps.scan("queue:${cdd.shuntId}")
                    }
            }
            .map { JsonUtils.fromJson<UserQueue>(it.value) }
            .sort { o1, o2 -> (o1.inQueueTime - o2.inQueueTime).toInt() }
            .flatMap {
                val queue = if (atomicBoolean.getAndSet(false)) -1 else atomicInteger.getAndIncrement()
                assignmentByQueueNum(queue, it.organizationId, it.shuntId, it.userId)
            }
            .then()
    }
}