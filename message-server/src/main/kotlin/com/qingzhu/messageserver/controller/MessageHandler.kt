package com.qingzhu.messageserver.controller

import com.qingzhu.messageserver.domain.dto.Message
import com.qingzhu.messageserver.service.MessageService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono

/**
 * 消息控制器
 */
@RestController
class MessageHandler(
        private val messageService: MessageService
) {
    /**
     * 这里不返回 Mono<ServerResponse> 是因为 kotlin 可以使用协程调用 suspend 方法
     */
    suspend fun send(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<Message>().transform(messageService::send)
                .flatMap { ok().build() }.awaitSingle()
    }
}