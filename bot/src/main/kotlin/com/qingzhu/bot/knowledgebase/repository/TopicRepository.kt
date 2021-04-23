package com.qingzhu.bot.knowledgebase.repository

import com.qingzhu.bot.knowledgebase.entity.Topic
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface TopicRepository : CoroutineSortingRepository<Topic, Long> {
    fun findByQuestion(question: String) : Flow<Topic>
}