package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.sendWithCallback
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap

/**
 * 自动踢人，超时关闭人工会话
 */
@Service
class AutoKickOutService(
    private val registerService: RegisterService,
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
) : CommandLineRunner {
    fun parseNumber(str: String?, defaultValue: Long): Long {
        return try {
            str?.toLong() ?: defaultValue
        } catch (_: Exception) {
            defaultValue
        }
    }

    override fun run(vararg args: String?) {
        val needReply = ConcurrentHashMap<Long, Boolean>()
        Flux.interval(Duration.ZERO, Duration.ofSeconds(30), Schedulers.boundedElastic())
            .doOnNext { _ ->
                MapUtils.getAllCustomerClient().toFlux()
                    .flatMap { messageService.findCustomerById(it.first, it.second) }
                    .flatMap {
                        staffAdminService.findDistinctTopPropByKey(it.organizationId, "sys.autoReply.timeout")
                            .map { prop ->
                                it to prop
                            }
                    }
                    .filter {
                        needReply[it.first.userId] = it.first.needReply
                        it.first.needReply && it.first.autoReply.not() && it.first.lastReplyTime.isBefore(
                            Instant.now().minus(parseNumber(it.second.value, 3), ChronoUnit.MINUTES)
                        )
                    }
                    .map { it.first }
                    .flatMap {
                        messageService.findConversationByUserId(it.organizationId, it.userId)
                            .flatMap { csd ->
                                val message = staffAdminService.findDistinctTopPropByKey(
                                    it.organizationId,
                                    "sys.autoReply.content"
                                )
                                    .flatMap { prop -> prop.value.toMono() }
                                    .switchIfEmpty(Mono.just("""温馨提醒：目前咨询较多，客服正在努力解答中，请按捺您激动的小心灵~"""))
                                    .map { text ->
                                        MessageDto(
                                            Message(
                                                organizationId = csd.organizationId,
                                                conversationId = csd.id,
                                                to = it.userId,
                                                from = csd.staffId,
                                                type = CreatorType.CUSTOMER,
                                                creatorType = CreatorType.STAFF,
                                                content = Content(
                                                    MessageType.TEXT,
                                                    SysCode.AUTO_REPLY,
                                                    // 把客户状态发送给客服
                                                    textContent = Content.TextContent(text)
                                                )
                                            )
                                        )
                                    }
                                messageService.send(message)
                            }
                    }
                    .publishOn(Schedulers.boundedElastic())
                    .thenMany(MapUtils.Time.getExpiredKey(CreatorType.CUSTOMER) {
                        val timeout = staffAdminService.findDistinctTopPropByKey(it, "sys.clientTimeout.timeout")
                            .mapNotNull { prop -> prop.value }
                            .block()
                        Duration.ofMinutes(parseNumber(timeout, 15))
                    }
                        .toFlux())
                    .filter {
                        // 如果在等待客服回应，就不关闭会话
                        needReply[it.id]?.not() ?: true
                    }
                    .doOnNext { MapUtils.Time.removeKey(it); needReply.remove(it.id) }
                    .flatMapSequential {
                        MapUtils.get(Key(it.organizationId, it.role, it.id)).map { socket -> it to socket }
                    }
                    .map { (key, socket) ->
                        // TODO 消息通过配置获取
                        socket.sendWithCallback<Void>(
                            SocketEvent.IO.closed,
                            WebSocketRequest.createRequest(socket.sessionId.toString(), "连接超时断开")
                        ) {
                            registerService.unRegisterCustomer(key.organizationId, key.id, CreatorType.SYS).subscribe()
                            MapUtils.remove(Key(key.organizationId, CreatorType.CUSTOMER, key.id), socket)
                            socket.disconnect()
                        }
                    }
                    .thenMany(MapUtils.Time.getExpiredKey(CreatorType.STAFF, Duration.ofMinutes(2)).toFlux())
                    .doOnNext { MapUtils.Time.removeKey(it) }
                    .map {
                        TODO("客服离线超时，发送关闭信号给其客户")
                    }
                    .subscribe()
            }
            .subscribe()
    }
}