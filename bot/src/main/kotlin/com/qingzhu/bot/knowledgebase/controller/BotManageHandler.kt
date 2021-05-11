package com.qingzhu.bot.knowledgebase.controller

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import com.qingzhu.bot.knowledgebase.service.BotManageService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBodyOrNull
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@RestController
class BotManageHandler(
    private val botManageService: BotManageService
) {
    suspend fun saveTopic(sr: ServerRequest): ServerResponse {
        return sr.awaitBodyOrNull<Topic>()?.let {
            val topic = botManageService.saveTopic(it)
            ServerResponse.ok().bodyValueAndAwait(topic)
        } ?: ServerResponse.ok().build().awaitSingle()
    }

    suspend fun saveBotConfig(sr: ServerRequest): ServerResponse {
        return sr.awaitBodyOrNull<BotConfig>()?.let {
            val botConfig = botManageService.saveBotConfig(it)
            ServerResponse.ok().bodyValueAndAwait(botConfig)
        } ?: ServerResponse.ok().build().awaitSingle()
    }

    suspend fun saveKnowledgeBase(sr: ServerRequest): ServerResponse {
        return sr.awaitBodyOrNull<KnowledgeBase>()?.let {
            val topic = botManageService.saveKnowledgeBase(it)
            ServerResponse.ok().bodyValueAndAwait(topic)
        } ?: ServerResponse.ok().build().awaitSingle()
    }

    suspend fun saveTopicCategory(sr: ServerRequest): ServerResponse {
        return sr.awaitBodyOrNull<TopicCategory>()?.let {
            val topic = botManageService.saveTopicCategory(it)
            ServerResponse.ok().bodyValueAndAwait(topic)
        } ?: ServerResponse.ok().build().awaitSingle()
    }
}