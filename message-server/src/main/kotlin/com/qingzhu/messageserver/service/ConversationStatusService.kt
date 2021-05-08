package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class ConversationStatusService(
        @Qualifier("hazelcastInstance")
        private val hazelcastInstance: HazelcastInstance,
        private val staffStatusService: StaffStatusService,
        private val clearStatusService: ClearStatusService,
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
            // 添加 EntryListener 到 IMap， 删除 entry 同时会删除该 userId 关联的会话和 redis zSet 消息
            statusMap.addEntryListener(clearStatusService, true)
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
                    statusMap.put(it.id, it, 15, TimeUnit.MINUTES)
                    staffStatusService.removeCustomer(it.organizationId,
                            it.staffId, it.userId)
                }
    }

    fun generate(conversationStatusDto: ConversationStatus): Mono<ConversationStatus> {
        return Mono.just(conversationStatusDto)
                .doOnNext { saveStatus(it) }
    }

    fun findByUserId(organizationId: Int, userId: Long): Mono<ConversationStatus> {
        val statusMap = getStatusMap(organizationId)
        val equalPredicate = EqualPredicate("userId", userId)
        @Suppress("UNCHECKED_CAST")
        return Mono.justOrEmpty(statusMap
                .values(equalPredicate as Predicate<Long, ConversationStatus>)
                .stream().findFirst())
    }

}