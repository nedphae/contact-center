package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
import com.qingzhu.bot.knowledgebase.repository.KnowledgeBaseRepository
import com.qingzhu.bot.knowledgebase.repository.TopicCategoryRepository
import com.qingzhu.bot.knowledgebase.repository.TopicRepository
import org.springframework.stereotype.Service

@Service
class BotManageService(
        private val topicRepository: TopicRepository,
        private val botConfigRepository: BotConfigRepository,
        private val knowledgeBaseRepository: KnowledgeBaseRepository,
        private val topicCategoryRepository: TopicCategoryRepository,
) {
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
}