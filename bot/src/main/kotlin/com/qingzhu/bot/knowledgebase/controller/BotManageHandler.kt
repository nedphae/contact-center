package com.qingzhu.bot.knowledgebase.controller

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import com.qingzhu.bot.knowledgebase.service.BotManageService
import com.qingzhu.common.security.awaitPrincipalTriple
import com.qingzhu.common.security.awaitPrincipalTripleWithBodyOrg
import com.qingzhu.common.security.password.toMd5Hex
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@RestController
class BotManageHandler(
    private val botManageService: BotManageService
) {
    suspend fun deleteTopicByIds(sr: ServerRequest): ServerResponse {
        val ids = sr.bodyToFlow<String>()
        botManageService.deleteTopicByIds(ids)
        return ok().build().awaitSingle()
    }

    suspend fun deleteBotConfigByIds(sr: ServerRequest): ServerResponse {
        val ids = sr.bodyToFlow<Long>()
        botManageService.deleteBotConfigByIds(ids)
        return ok().build().awaitSingle()
    }

    suspend fun deleteKnowledgeBaseByIds(sr: ServerRequest): ServerResponse{
        val ids = sr.bodyToFlow<Long>()
        botManageService.deleteKnowledgeBaseById(ids)
        return ok().build().awaitSingle()
    }

    suspend fun deleteTopicCategoryByIds(sr: ServerRequest): ServerResponse {
        val ids = sr.bodyToFlow<Long>()
        botManageService.deleteTopicCategoryById(ids)
        return ok().build().awaitSingle()
    }

    suspend fun saveTopic(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        return sr.awaitBodyOrNull<Topic>()?.let {
            it.md5 = it.question.toMd5Hex()
            it.organizationId = organizationId
            val topic = botManageService.saveTopic(it)
            ok().bodyValueAndAwait(topic)
        } ?: ok().build().awaitSingle()
    }

    suspend fun saveBotConfig(sr: ServerRequest): ServerResponse {
        return sr.awaitPrincipalTripleWithBodyOrg<BotConfig>()?.let {
            val botConfig = botManageService.saveBotConfig(it)
            ok().bodyValueAndAwait(botConfig)
        } ?: ok().build().awaitSingle()
    }

    suspend fun saveKnowledgeBase(sr: ServerRequest): ServerResponse {
        return sr.awaitPrincipalTripleWithBodyOrg<KnowledgeBase>()?.let {
            val topic = botManageService.saveKnowledgeBase(it)
            ok().bodyValueAndAwait(topic)
        } ?: ok().build().awaitSingle()
    }

    suspend fun saveTopicCategory(sr: ServerRequest): ServerResponse {
        return sr.awaitPrincipalTripleWithBodyOrg<TopicCategory>()?.let {
            val topic = botManageService.saveTopicCategory(it)
            ok().bodyValueAndAwait(topic)
        } ?: ok().build().awaitSingle()
    }

    suspend fun findAllTopic(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        return ok().bodyAndAwait(botManageService.findAllTopic(organizationId!!))
    }

    suspend fun findAllBotConfig(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        return ok().bodyAndAwait(botManageService.findAllBotConfig(organizationId!!))
    }

    suspend fun findAllKnowledgeBase(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        return ok().bodyAndAwait(botManageService.findAllKnowledgeBase(organizationId!!))
    }

    suspend fun findAllTopicCategory(sr: ServerRequest): ServerResponse {
        val (organizationId, _, _) = sr.awaitPrincipalTriple()
        return ok().bodyAndAwait(botManageService.findAllTopicCategory(organizationId!!))
    }
}