package com.qingzhu.bot.knowledgebase.repository

import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface TopicRepository : CoroutineSortingRepository<Topic, Long> {
    fun findByKnowledgeBaseIdAndQuestion(knowledgeBaseId: Long, question: String) : Flow<Topic>
}