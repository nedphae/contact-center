package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.entity.Topic
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
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
        private val botConfigRepository: BotConfigRepository,
) {
    /**
     * TODO 后期增加无答案转人工？
     */
    suspend fun findAnswerByQuestion(botId: Long, question: String): String? {
        //根据 bot Id 获取映射的 KnowledgeBaseId
        val botConfig = botConfigRepository.findByBotId(botId)
        if (botConfig != null) {
            val flow = topicRepository.findByKnowledgeBaseIdAndQuestion(botConfig.knowledgeBaseId, question)
            var first = flow.firstOrNull()
            if (first != null && first.answer == null && first.refId != null) {
                val refId = first.refId!!
                first = topicRepository.findById(refId)
            }
            if (first != null) {
                return first.answer
            }
        }
        return null
    }

    suspend fun saveTopic(topic: Topic?): Topic? {
        if (topic != null) {
            return topicRepository.save(topic)
        }
        return topic
    }

    // TODO 增加 redis cache
}
