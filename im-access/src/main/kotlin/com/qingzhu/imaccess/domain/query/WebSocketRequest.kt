package com.qingzhu.imaccess.domain.query

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.message.Header
import com.qingzhu.imaccess.socketio.AckBuilder
import com.qingzhu.imaccess.socketio.errorAck
import com.qingzhu.imaccess.socketio.messageAck
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import java.util.*


/**
 * websocket 请求格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
open class WebSocketRequest<T>(
    /** 执行的命令 */
    // val path: String?,
    /** 消息头 */
    val header: Header,
    /** 消息体 */
    val body: T
) : java.io.Serializable {

    companion object {
        @JvmStatic
        private val serialVersionUID = 741L

        fun <T> createRequest(sid: String, body: T): WebSocketRequest<T> {
            return WebSocketRequest(
                Header(UUID.randomUUID().toString(), sid),
                body
            )
        }
    }

    fun toMonoMonad(socketIOClient: SocketIOClient): Mono<T> {
        return Mono.just(this)
            .doOnNext {
                it.header.sid = socketIOClient.sessionId.toString()
            }.map { it.body }
    }
}

fun <T> Mono<T>.subscribeWithoutData(ackRequest: AckRequest, request: WebSocketRequest<*>) {
    this.subscribe({
        messageAck(ackRequest, request.header)
    }) {
        errorAck(ackRequest, request.header, it)
    }
}

fun <T> Mono<T>.subscribeWithData(ackRequest: AckRequest, request: WebSocketRequest<*>) {
    this.subscribe({
        // 响应服务器生成的消息ID
        AckBuilder<T>(ackRequest).header(request.header).body(it).send()
    }) {
        errorAck(ackRequest, request.header, it)
    }
}

fun <T, R> Mono<T>.messageSubscribe(ackRequest: AckRequest, request: WebSocketRequest<*>, block: (T) -> R) {
    this
        // 清理过滤的消息
        // 如果有过滤的消息就调用
        .doOnDiscard(Message::class.java) {
            // 过滤没设置收件人的消息，或其他有问题消息并返回 400
            AckBuilder<Message>(ackRequest).header(request.header).httpStatus(HttpStatus.BAD_REQUEST).send()
        }
        // 发送到消息服务器
        .map { block(it) }
        .subscribeWithData(ackRequest, request)
}
