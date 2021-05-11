package com.qingzhu.imaccess.socketio

import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

/**
 * websocket 测试类
 */
class EchoHandler : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {
        return session
            .send(session.receive()
                .map { "RECEIVED ON SERVER :: " + it.payloadAsText }
                .map(session::textMessage)
            )
    }
}