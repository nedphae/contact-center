package com.qingzhu.bot.knowledgebase.repository

import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface BotConfigRepository : CoroutineSortingRepository<BotConfig, Long> {
    suspend fun findByBotId(botId: Long): BotConfig?
    suspend fun deleteAllByIdIn(id: List<Long>)
    fun findAllByOrganizationId(organizationId: Int): Flow<BotConfig>
}

interface KnowledgeBaseRepository : CoroutineSortingRepository<KnowledgeBase, Long> {
    suspend fun deleteAllByIdIn(id: List<Long>)
    fun findAllByOrganizationId(organizationId: Int): Flow<KnowledgeBase>
}

interface TopicCategoryRepository : CoroutineSortingRepository<TopicCategory, Long> {
    suspend fun deleteAllByIdIn(id: List<Long>)
    fun findAllByOrganizationId(organizationId: Int): Flow<TopicCategory>
}
