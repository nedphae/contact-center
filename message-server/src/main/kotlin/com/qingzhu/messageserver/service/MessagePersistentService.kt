package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.messageserver.domain.entity.Conversation
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.mapper.ChatMessageMapper
import com.qingzhu.messageserver.mapper.ConversationMapper
import com.qingzhu.messageserver.repository.search.ConversationRepository
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 数据持久化服务
 */
@Service
class MessagePersistentService(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val conversationRepository: ConversationRepository,
) {
    private val zSet: ReactiveZSetOperations<String, String> = redisTemplate.opsForZSet()

    /**
     * 持久化会话和聊天消息
     */
    fun convPersistent(conversationStatus: ConversationStatus): Mono<Conversation> {
        // 保存聊天消息
        val minMsgId = conversationStatus.id
        val redisKey = "${conversationStatus.organizationId}:${CreatorType.CUSTOMER.name.toLowerCase()}:${conversationStatus.userId}"
        val chatMessageList =
            zSet.reverseRangeByScore(redisKey, Range.closed(minMsgId.toDouble(), Double.MAX_VALUE))
                .map { msg ->
                    ChatMessageMapper.mapper.mapToFromMessage(JsonUtils.fromJson(msg))
                }
                .doOnNext {

                }
        val conversation = ConversationMapper.mapper.mapFromStatusWithEnum(conversationStatus)
        return chatMessageList
            .collectList()
            .flatMap { msg ->
                conversation.chatMessages = msg
                conversationRepository.save(conversation)
            }
    }
}
