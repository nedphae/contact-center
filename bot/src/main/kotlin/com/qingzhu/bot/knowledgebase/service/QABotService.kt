package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.entity.Topic
import com.qingzhu.bot.knowledgebase.repository.TopicRepository
import kotlinx.coroutines.flow.firstOrNull
import org.springframework.stereotype.Service

/**
 * 问答机器人服务
 * 基于 ES 的关键字查询服务
 */
@Service
class QABotService(
        private val topicRepository: TopicRepository,
) {
    suspend fun findByQuestion(question: String): Topic? {
        val flow = topicRepository.findByQuestion(question)
        var first = flow.firstOrNull()
        if (first != null && first.answer == null && first.refId != null) {
            val refId = first.refId!!
            first = topicRepository.findById(refId)
        }
        return first
    }

    suspend fun saveTopic(topic: Topic?): Topic? {
        if (topic != null) {
            return topicRepository.save(topic)
        }
        return topic
    }
}