package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.concurrent.TimeUnit

@Service
class ConversationStatusService(
    @Qualifier("hazelcastInstance")
    private val hazelcastInstance: HazelcastInstance,
    private val staffStatusService: StaffStatusService,
    private val messagePersistentService: MessagePersistentService,
) {

    private fun getStatusMap(organizationId: Int) =
        hazelcastInstance.getMap<Long, ConversationStatus>("$organizationId:conversation")

    /**
     * 设置客服状态
     */
    fun saveStatus(conversationStatus: ConversationStatus) {
        val statusMap = getStatusMap(conversationStatus.organizationId)
        if (statusMap.isEmpty) {
            statusMap.addIndex(IndexType.HASH, "staffId")
            statusMap.addIndex(IndexType.HASH, "userId")
        }
        statusMap.put(conversationStatus.id, conversationStatus, 1, TimeUnit.HOURS)
    }

    /**
     * 结束会话
     */
    fun endConversation(conversationStatus: ConversationStatus): Mono<ConversationStatus> {
        val statusMap = getStatusMap(conversationStatus.organizationId)
        return Mono.justOrEmpty(statusMap[conversationStatus.id])
            .doOnNext {
                it.endConversation()
                if (it.interaction == 0) {
                    // 机器人会话不保留了
                    statusMap.remove(it.id)
                } else {
                    statusMap.put(it.id, it, 10, TimeUnit.MINUTES)
                }
                staffStatusService.removeCustomer(
                    it.organizationId,
                    it.staffId, it.userId
                )
            }
            .flatMap {
                messagePersistentService.convPersistent(it)
                    .map { _ -> it }
            }
    }

    /**
     * 生成会话
     */
    fun generate(conversationStatusDto: ConversationStatus): Mono<ConversationStatus> {
        return Mono.just(conversationStatusDto)
            .doOnNext { saveStatus(it) }
    }

    fun statisticsByChatMessage(
        organizationId: Int,
        userId: Long,
        message: Message
    ): Mono<ConversationStatus> {
        return findLatestByUserId(organizationId, userId)
            .doOnNext { cs ->
                if (message.creatorType == CreatorType.CUSTOMER && cs.clientFirstMessageTime == null) {
                    cs.clientFirstMessageTime = Instant.now()
                }
                val lastReplyTime = cs.clientFirstMessageTime
                if (message.creatorType == CreatorType.STAFF && lastReplyTime != null) {
                    if (cs.staffFirstReplyTime == null) {
                        cs.staffFirstReplyTime = Instant.now()
                        cs.firstReplyCost = Instant.now().epochSecond - lastReplyTime.epochSecond
                        cs.avgRespDuration = cs.firstReplyCost
                    } else {
                        cs.avgRespDuration =
                            (cs.avgRespDuration + Instant.now().epochSecond - lastReplyTime.epochSecond) / 2
                    }
                }
                saveStatus(cs)
            }
    }

    fun findLatestByUserId(organizationId: Int, userId: Long): Mono<ConversationStatus> {
        return Mono.create {
            val statusMap = getStatusMap(organizationId)

            @Suppress("UNCHECKED_CAST")
            val equalPredicate = EqualPredicate("userId", userId) as Predicate<Long, ConversationStatus>
            val pagePredicate = Predicates.pagingPredicate(
                equalPredicate,
                // desc for conversation id
                { o1, o2 -> (o2?.key?.minus(o1?.key ?: 0) ?: 0).toInt() }, 1
            )
            it.success(statusMap.values(pagePredicate).firstOrNull())
        }
    }

}