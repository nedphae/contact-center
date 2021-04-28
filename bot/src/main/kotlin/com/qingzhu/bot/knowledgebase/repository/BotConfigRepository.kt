package com.qingzhu.bot.knowledgebase.repository

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface BotConfigRepository : CoroutineSortingRepository<BotConfig, Long> {
    suspend fun findByBotId(botId: Long): BotConfig?
}

interface KnowledgeBaseRepository : CoroutineSortingRepository<KnowledgeBase, Long>

interface TopicCategoryRepository : CoroutineSortingRepository<TopicCategory, Long>
