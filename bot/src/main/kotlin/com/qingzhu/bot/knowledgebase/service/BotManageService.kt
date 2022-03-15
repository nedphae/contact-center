package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
import com.qingzhu.bot.knowledgebase.repository.KnowledgeBaseRepository
import com.qingzhu.bot.knowledgebase.repository.TopicCategoryRepository
import com.qingzhu.bot.knowledgebase.repository.search.TopicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Service

@Service
class BotManageService(
    private val topicRepository: TopicRepository,
    private val botConfigRepository: BotConfigRepository,
    private val knowledgeBaseRepository: KnowledgeBaseRepository,
    private val topicCategoryRepository: TopicCategoryRepository,
) {

    suspend fun deleteTopicByIds(ids: Flow<String>) {
        val list4Delete = topicRepository.findAllById(ids)
        topicRepository.deleteAll(list4Delete)
    }

    suspend fun deleteBotConfigByIds(ids: Flow<Long>) {
        botConfigRepository.deleteAllByIdIn(ids.toList())
    }

    suspend fun deleteKnowledgeBaseById(ids: Flow<Long>) {
        knowledgeBaseRepository.deleteAllByIdIn(ids.toList())
    }

    suspend fun deleteTopicCategoryById(ids: Flow<Long>) {
        topicCategoryRepository.deleteAllByIdIn(ids.toList())
    }

    suspend fun saveTopic(topic: Topic): Topic {
        return topicRepository.save(topic)
    }

    suspend fun saveBotConfig(botConfig: BotConfig): BotConfig {
        return botConfigRepository.save(botConfig)
    }

    suspend fun saveKnowledgeBase(knowledgeBase: KnowledgeBase): KnowledgeBase {
        return knowledgeBaseRepository.save(knowledgeBase)
    }

    suspend fun saveTopicCategory(topicCategory: TopicCategory): TopicCategory {
        return topicCategoryRepository.save(topicCategory)
    }

    suspend fun findAllTopic(organizationId: Int): Flow<Topic> {
        return topicRepository.findAllByOrganizationId(organizationId)
    }

    suspend fun findAllBotConfig(organizationId: Int): Flow<BotConfig> {
        return botConfigRepository.findAllByOrganizationId(organizationId)
    }

    suspend fun findAllKnowledgeBase(organizationId: Int): Flow<KnowledgeBase> {
        return knowledgeBaseRepository.findAllByOrganizationId(organizationId)
    }

    suspend fun findAllTopicCategory(organizationId: Int): Flow<TopicCategory> {
        return topicCategoryRepository.findAllByOrganizationId(organizationId)
    }
}