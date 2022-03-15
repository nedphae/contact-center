package com.qingzhu.messageserver.controller

import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.message.getChatMessageSnowFlake
import com.qingzhu.common.security.awaitPrincipalTriple
import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.common.util.getOrganizationIdAnd
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

    suspend fun syncBotMessage(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<Message>().flatMap(messageService::syncBotMessage)
            .flatMap {
                if (it) ok().build() else status(HttpStatus.NOT_ACCEPTABLE).build()
            }.awaitSingle()
    }

    suspend fun sendAssignmentEvent(sr: ServerRequest): ServerResponse {
        return sr.bodyToMono<ConversationStatus>().transform(messageService::sendAssignmentEvent)
            .then(ok().build()).awaitSingle()
    }

    suspend fun search(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        return sr.bodyToMono<ConversationQuery>()
            .doOnNext { it.organizationId = orgId }
            .flatMap { ok().body(messagePersistentService.searchConv(it)) }
            .awaitSingle()
    }

    suspend fun hasHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        val (orgId) = sr.awaitGetOrganizationId()
        return messagePersistentService.hasHistoryMessage(orgId!!, userId)
            .flatMap {
                ok().bodyValue(it)
            }.awaitSingle()
    }

    suspend fun loadHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        val (orgId) = sr.awaitGetOrganizationId()
        val lastSeqId = sr
            .queryParam("lastSeqId")
            .filter { it.isNotBlank() }
            .map { it.toLong() }.orElse(getChatMessageSnowFlake().getNextSequenceId())
        val count = sr
            .queryParam("pageSize")
            .filter { it.isNotBlank() }
            .map { it.toInt() }
            .orElse(20)
        return messagePersistentService.loadHistoryMessage(orgId!!, userId, lastSeqId, count)
            .flatMap {
                ok().bodyValue(it)
            }.awaitSingle()
    }

    /**
     * 同步聊天消息
     * 每5秒同步一次，如果是第一次同步，获取最近的20条消息
     */
    suspend fun syncHistoryMessage(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("userId").map { it.toLong() }.orElse(null)
        val lastSeqId = sr.queryParam("lastSeqId").map { it.toLong() }.orElse(null)
        val (orgId, _, _) = sr.awaitPrincipalTriple()
        return if (orgId != null) {
            if (lastSeqId == null) {
                // 没有发同步id 就获取最近20 条
                messagePersistentService.loadHistoryMessage(orgId, userId, getChatMessageSnowFlake().getNextSequenceId(), 20)
            } else {
                messagePersistentService.syncHistoryMessage(orgId, userId, lastSeqId)
            }.flatMap {
                ok().bodyValue(it)
            }.awaitSingle()
        } else ok().build().awaitSingle()
    }

    suspend fun findLatestStaffConvByUserId(sr: ServerRequest): ServerResponse {
        return sr.getOrganizationIdAnd("userId") { oid, userId ->
            ok().body(messagePersistentService.findLatestStaffConvByUserId(oid, userId.toLong()))
        }
    }
}