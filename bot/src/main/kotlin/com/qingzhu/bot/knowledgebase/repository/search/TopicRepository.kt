package com.qingzhu.bot.knowledgebase.repository.search

import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface TopicRepository : CoroutineSortingRepository<Topic, String> {
    fun findByKnowledgeBaseIdAndQuestion(knowledgeBaseId: Long, question: String): Flow<Topic>
}