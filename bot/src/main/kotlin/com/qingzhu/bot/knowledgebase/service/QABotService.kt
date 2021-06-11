package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.knowledgebase.domain.dto.MessagePair
import com.qingzhu.bot.knowledgebase.domain.view.ChatUIContent
import com.qingzhu.bot.knowledgebase.domain.view.ChatUIMessage
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
import com.qingzhu.bot.knowledgebase.repository.search.TopicRepository
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.reactive.awaitSingleOrDefault
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 问答机器人服务
 * 基于 ES 的关键字查询服务
 */
@Service
class QABotService(
    private val topicRepository: TopicRepository,
    private val botConfigRepository: BotConfigRepository,
    private val messageFilterService: MessageFilterService,
) {

    suspend fun findAnswerByQuestion(
        userId: Long,
        botId: Long,
        conversationId: Long,
        question: ChatUIMessage
    ): ChatUIMessage? {
        //根据 bot Id 获取映射的 KnowledgeBaseId
        val botConfig = botConfigRepository.findByBotId(botId)
        if (botConfig != null) {
            val requestMessage = ChatUIMessage.createTextMessage(
                botConfig.organizationId,
                conversationId,
                from = userId,
                to = botId,
                question
            )
            var answer = botConfig.noAnswerReply
            var sysCode: SysCode? = SysCode.NO_ANSWER
            if (requestMessage.content.contentType == MessageType.TEXT && requestMessage.content.textContent != null) {
                val flow = topicRepository.findByKnowledgeBaseIdAndQuestion(
                    botConfig.knowledgeBaseId,
                    requestMessage.content.textContent!!.text
                )
                var first = flow.firstOrNull()
                // 查询相似问题
                if (first != null && first.answer == null && first.refId != null) {
                    val refId = first.refId!!
                    first = topicRepository.findById(refId)
                }
                if (first != null) {
                    sysCode = null
                    answer = first.answer ?: botConfig.noAnswerReply
                }
            }

            val responseMessage = ChatUIMessage.createTextMessage(
                botConfig.organizationId,
                conversationId,
                from = botId,
                to = userId,
                answer,
                sysCode
            )
            return messageFilterService
                .filter(Mono.just(MessagePair(requestMessage, responseMessage)))
                .map { it.answerMessage }
                .map(ChatUIMessage::fromMessage)
                .awaitSingleOrDefault(ChatUIMessage(type = "cmd", content = ChatUIContent("agent_join")))
        }
        return ChatUIMessage(content = ChatUIContent("无法找到机器人，请联系管理员"))
    }

    // TODO 增加 redis cache
}
