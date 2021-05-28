package com.qingzhu.messageserver.controller

import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.domain.query.ConversationQuery
import com.qingzhu.messageserver.service.MessagePersistentService
import com.qingzhu.messageserver.service.MessageService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.bodyToMono

/**
 * 消息控制器
 */
@RestController
class MessageHandler(
    private val messageService: MessageService,
    private val messagePersistentService: MessagePersistentService,
) {
    /**
     * 这里不返回 Mono<ServerResponse> 是因为 kotlin 可以使用协程调用 suspend 方法
     */
    suspend fun send(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<MessageDto>().transform(messageService::send)
            .flatMap {
                if (it) ok().build() else status(HttpStatus.NOT_ACCEPTABLE).build()
            }.awaitSingle()
    }

    suspend fun sendAssignmentEvent(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<ConversationStatus>().transform(messageService::sendAssignmentEvent)
            .then(ok().build()).awaitSingle()
    }

    suspend fun search(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<ConversationQuery>()
            .flatMap { ok().body(messagePersistentService.searchConv(it)) }
            .awaitSingle()
    }
}