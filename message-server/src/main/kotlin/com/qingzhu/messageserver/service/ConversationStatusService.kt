package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.Predicates
import com.hazelcast.query.impl.predicates.EqualPredicate
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
                statusMap.put(it.id, it, 15, TimeUnit.MINUTES)
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

    fun findLatestByUserId(organizationId: Int, userId: Long): Mono<ConversationStatus> {
        val statusMap = getStatusMap(organizationId)

        @Suppress("UNCHECKED_CAST")
        val equalPredicate = EqualPredicate("userId", userId) as Predicate<Long, ConversationStatus>
        val pagePredicate = Predicates.pagingPredicate(equalPredicate,
            // desc for conversation id
            { o1, o2 -> (o2?.key?.minus(o1?.key ?: 0) ?: 0).toInt() }, 1
        )
        return Mono.justOrEmpty(
            statusMap
                .values(pagePredicate)
                .stream().findFirst()
        )
    }

}