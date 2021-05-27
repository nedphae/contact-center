package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.view.Message
import com.qingzhu.bot.knowledgebase.domain.view.TextContent
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
import com.qingzhu.bot.knowledgebase.repository.search.TopicRepository
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
    suspend fun findAnswerByQuestion(userId: Long, botId: Long, question: String): Message? {
        //根据 bot Id 获取映射的 KnowledgeBaseId
        val botConfig = botConfigRepository.findByBotId(botId)
        if (botConfig != null) {
            val flow = topicRepository.findByKnowledgeBaseIdAndQuestion(botConfig.knowledgeBaseId, question)
            var first = flow.firstOrNull()
            // 查询相似问题
            if (first != null && first.answer == null && first.refId != null) {
                val refId = first.refId!!
                first = topicRepository.findById(refId)
            }
            return if (first != null) {
                first.answer?.let {
                    Message(content = TextContent(it))
                } ?: Message(content = TextContent(botConfig.noAnswerReply))
            } else {
                Message(content = TextContent(botConfig.noAnswerReply))
            }
        }
        return Message(content = TextContent("无法找到机器人，请联系管理员"))
    }

    // TODO 增加 redis cache
}
