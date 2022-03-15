package com.qingzhu.bot.knowledgebase.controller

import com.qingzhu.bot.knowledgebase.service.QABotService
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import com.qingzhu.common.util.JsonUtils
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@RestController
class QABotHandler(
    private val qaBotService: QABotService,
) {
    suspend fun getAnswer(sr: ServerRequest): ServerResponse {
        val userId = sr.queryParam("u").map { it.toLong() }.orElse(null)
        val botId = sr.queryParam("b").map { it.toLong() }.orElse(null)
        val question = sr.queryParam("q").map { JsonUtils.fromJson<ChatUIMessage>(it) }.orElse(null)
        val conversationId = sr.queryParam("c").map { it.toLong() }.orElse(null)
        if (userId != null && botId != null && question != null && conversationId != null) {
            val answer = qaBotService.findAnswerByQuestion(userId, botId, conversationId, question)
            if (answer.isEmpty().not()) {
                return ok().bodyValueAndAwait(answer)
            }
        }
        return ok().build().awaitSingle()
    }
}