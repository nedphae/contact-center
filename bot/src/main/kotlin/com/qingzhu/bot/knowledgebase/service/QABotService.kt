package com.qingzhu.bot.knowledgebase.service

import arrow.core.extensions.list.applicative.just
import com.qingzhu.bot.knowledgebase.domain.dto.MessagePair
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.repository.BotConfigRepository
import com.qingzhu.bot.knowledgebase.repository.search.TopicRepository
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.dto.Card
import com.qingzhu.common.domain.shared.msg.dto.CardData
import com.qingzhu.common.domain.shared.msg.dto.ChatUIContent
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 * 问答机器人服务
 * 基于 ES 的关键字查询服务
 */
@Service
class QABotService(
    private val topicRepository: TopicRepository,
    private val botConfigRepository: BotConfigRepository,
    private val messageFilterService: MessageFilterService,
    private val staffAdminService: StaffAdminService,
) {

    suspend fun findAnswerByQuestion(
        userId: Long,
        botId: Long,
        conversationId: Long,
        question: ChatUIMessage
    ): List<ChatUIMessage> {
        //根据 bot Id 获取映射的 KnowledgeBaseId
        val botConfig = botConfigRepository.findByBotId(botId)
        val botInfo = staffAdminService.findStaffInfo(botId).awaitSingle()
        if (botConfig != null) {
            val requestMessage = ChatUIMessage.createMessage(
                botConfig.organizationId!!,
                conversationId,
                from = userId,
                to = botId,
                question,
                nickName = botInfo.nickName,
            )
            var answerList = listOf(botConfig.getAnswer())
            var connectTopicFlow: Flow<Topic>? = null
            var sysCode: SysCode? = SysCode.NO_ANSWER
            if (requestMessage.content.contentType == MessageType.TEXT && requestMessage.content.textContent != null) {
                val flow = topicRepository.findByKnowledgeBaseIdAndQuestion(
                    botConfig.knowledgeBaseId,
                    requestMessage.content.textContent!!.text
                ).asFlow()
                var first = flow.firstOrNull()?.content
                // 查询相似问题
                if (first != null && first.answer == null && first.refId != null) {
                    val refId = first.refId!!
                    first = topicRepository.findById(refId)
                }
                if (first != null) {
                    sysCode = null
                    answerList = first.answer ?: answerList
                    if (!first.connectIds.isNullOrEmpty()) {
                        connectTopicFlow = topicRepository.findAllById(first.connectIds!!)
                    }
                }
            }

            val responseMessage = answerList.map {
                ChatUIMessage.createMessage(
                    botConfig.organizationId!!,
                    conversationId,
                    from = botId,
                    to = userId,
                    it.toChatUIMessage(),
                    type = CreatorType.CUSTOMER,
                    creatorType=  CreatorType.STAFF,
                    sysCode,
                    nickName = botInfo.nickName,
                )
            }

            var result = messageFilterService
                .filter(Mono.just(MessagePair(requestMessage, responseMessage)))
                .flatMapIterable { it.answerMessage }
                .map(ChatUIMessage::fromMessage)
                .switchIfEmpty(Flux.just(ChatUIMessage(type = "cmd", content = ChatUIContent("agent_join"))))
            // 将关联问题分装成卡片 recommend
            connectTopicFlow?.map { it.question }?.toList()?.map {
                CardData(title = it)
            }?.also {
                result = result.concatWith(
                    ChatUIMessage(
                        type = "card",
                        content = ChatUIContent(code = "recommend", data = Card(list = it))
                    ).toMono()
                )
            }
            return result.collectList().awaitSingle()
        }
        return ChatUIMessage(content = ChatUIContent("无法找到机器人，请联系管理员")).just()
    }

    // TODO 增加 redis cache
}
