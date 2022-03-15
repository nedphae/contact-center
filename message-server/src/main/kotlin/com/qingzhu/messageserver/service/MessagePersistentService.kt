package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.messageserver.domain.entity.ChatMessage
import com.qingzhu.messageserver.domain.entity.Conversation
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.domain.query.ConversationQuery
import com.qingzhu.messageserver.mapper.ChatMessageMapper
import com.qingzhu.messageserver.mapper.ConversationMapper
import com.qingzhu.messageserver.repository.ChatMessagePORepository
import com.qingzhu.messageserver.repository.search.ConversationRepository
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import org.springframework.data.domain.*
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.redis.connection.RedisZSetCommands
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

/**
 * 数据持久化服务
 */
@Service
class MessagePersistentService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val conversationRepository: ConversationRepository,
    private val reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate,
    private val dispatchingCenter: DispatchingCenter,
    private val chatMessagePORepository: ChatMessagePORepository,
) {
    private val zSet: ReactiveZSetOperations<String, String> = redisTemplate.opsForZSet()

    /**
     * 持久化会话和聊天消息
     */
    fun convPersistent(conversationStatus: ConversationStatus): Mono<Conversation> {
        // 保存聊天消息
        val minMsgId = conversationStatus.id
        val redisKey =
            "msg:${conversationStatus.organizationId}:${CreatorType.CUSTOMER.name.toLowerCase()}:${conversationStatus.userId}"
        val chatMessageList =
            zSet.reverseRangeByScore(redisKey, Range.closed(minMsgId.toDouble(), Double.MAX_VALUE))
                .map { msg ->
                    ChatMessageMapper.mapper.mapToFromMessage(JsonUtils.fromJson(msg))
                }
                .doOnNext {
                    // 清理 聊天消息 设置 缓存 2 天
                    redisTemplate.expire(redisKey, Duration.ofDays(3))
                }
        val conversation = ConversationMapper.mapper.mapFromStatusWithEnum(conversationStatus)
        val user = dispatchingCenter.findCustomer(conversation.organizationId, conversation.userId)
        return chatMessageList
            .collectList()
            .flatMap { msg ->
                conversation.setChatMessageAndStatistics(msg)
                user
                    .map {
                        conversation.userName = it.name
                        conversation
                    }
                    .flatMap {
                        conversationRepository.save(it)
                    }
            }
    }

    fun searchConv(conversationQuery: ConversationQuery): Mono<Page<SearchHit<Conversation>>> {
        return if (conversationQuery.organizationId != null) {
            reactiveElasticsearchTemplate
                .searchForPage(
                    conversationQuery.buildSearchQuery().build(),
                    Conversation::class.java
                )
                // 缩小 SearchHit 导致的序列化太大
                .map { PageImpl(it.content, it.pageable, it.totalElements) }
        } else {
            Mono.just(Page.empty())
        }
    }

    /**
     * 根据 [userId] 获取最近一次的人工会话
     */
    fun findLatestStaffConvByUserId(organizationId: Int, userId: Long): Mono<Conversation> {
        return conversationRepository.findFirstByOrganizationIdAndUserIdAndInteractionOrderByStartTimeDesc(organizationId, userId)
    }

    fun hasHistoryMessage(organizationId: Int, userId: Long): Mono<Boolean> {
        return zSet.size("msg:$organizationId:customer:$userId")
            .flatMap {
                if (it == 0L) {
                    chatMessagePORepository.countAll(organizationId, userId.toString())
                        .map { count ->
                            count != 0L
                        }
                } else Mono.just(true)
            }
    }

    /**
     * 手动同步聊天小i下
     */
    fun syncHistoryMessage(
        organizationId: Int,
        userId: Long,
        lastSeqId: Long,
    ): Mono<Slice<ChatMessage>> {
        return zSet
            .reverseRangeByScore(
                "msg:$organizationId:customer:$userId",
                Range.rightOpen(lastSeqId.toDouble(), Double.MAX_VALUE)
            )
            .map {
                JsonUtils.fromJson<ChatMessage>(it)
            }
            .collectList()
            .map {
                SliceImpl(it, CassandraPageRequest.first(it.size), false)
            }
    }

    fun loadHistoryMessage(
        organizationId: Int,
        userId: Long,
        lastSeqId: Long,
        pageSize: Int
    ): Mono<Slice<ChatMessage>> {
        val chatMessageList = zSet
            .reverseRangeByScore(
                "msg:$organizationId:customer:$userId",
                Range.rightOpen(Double.MIN_VALUE, lastSeqId.toDouble()),
                RedisZSetCommands.Limit().count(pageSize + 1)
            )
            .map {
                JsonUtils.fromJson<ChatMessage>(it)
            }
            .collectList()
        val page = CassandraPageRequest.first(pageSize)
        return chatMessageList
            .flatMap { list ->
                when {
                    list.size == 0 -> {
                        chatMessagePORepository
                            .findAllBySeqId(
                                organizationId,
                                userId.toString(),
                                lastSeqId,
                                page
                            )
                            .map { slice ->
                                SliceImpl(slice.content.map { it.toChatMessage() }, page, slice.hasNext())
                            }
                    }
                    list.size - 1 == pageSize -> {
                        list.removeLast()
                        SliceImpl(list, page, true).toMono()
                    }
                    else -> {
                        when (val size = pageSize - list.size) {
                            0 -> {
                                chatMessagePORepository.countAll(organizationId, userId.toString())
                                    .map { count ->
                                        count != 0L
                                    }
                                    .map {
                                        SliceImpl(list, page, it)
                                    }
                            }
                            else -> {
                                chatMessagePORepository.findAllBySeqId(
                                    organizationId,
                                    userId.toString(),
                                    list.last().seqId,
                                    CassandraPageRequest.first(size)
                                ).map { slice ->
                                    list.addAll(slice.content.map { it.toChatMessage() })
                                    SliceImpl(list, page, slice.hasNext())
                                }
                            }
                        }
                    }
                }
            }
    }
}
