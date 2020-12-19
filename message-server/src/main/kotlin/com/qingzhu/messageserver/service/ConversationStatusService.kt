package com.qingzhu.messageserver.service

import com.hazelcast.config.IndexType
import com.hazelcast.core.HazelcastInstance
import com.hazelcast.query.Predicate
import com.hazelcast.query.impl.predicates.EqualPredicate
import com.qingzhu.messageserver.domain.dto.ConversationBaseStatusDto
import com.qingzhu.messageserver.domain.dto.StaffChangeStatusDto
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit

@Service
class ConversationStatusService(
        @Qualifier("hazelcastInstance")
        private val hazelcastInstance: HazelcastInstance,
        private val clearStatusService: ClearStatusService,
        private val staffStatusService: StaffStatusService
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
            statusMap.addEntryListener(clearStatusService, true)
        }
        statusMap.put(conversationStatus.id, conversationStatus, 2, TimeUnit.HOURS)
    }

    fun setRemove(organizationId: Int, id: Long) {
        val statusMap = getStatusMap(organizationId)
        val conversationStatus = statusMap[id]
        if (conversationStatus != null) {
            statusMap.put(conversationStatus.id, conversationStatus, 1, TimeUnit.HOURS)
            staffStatusService.removeCustomer(conversationStatus.organizationId,
                    conversationStatus.staffId, conversationStatus.userId)
        }
    }

    fun generate(): Mono<ConversationStatus> {

    }

    fun findByUserId(organizationId: Int, userId: Long): Mono<ConversationStatus> {
        val statusMap = getStatusMap(organizationId)
        val equalPredicate = EqualPredicate("userId", userId)
        @Suppress("UNCHECKED_CAST")
        return Mono.justOrEmpty(statusMap
                .values(equalPredicate as Predicate<Long, ConversationStatus>)
                .stream().findFirst())
    }

    fun assignment(conversationBaseStatusDto: ConversationBaseStatusDto): Mono<ConversationStatus> {
        return findByUserId(conversationBaseStatusDto.organizationId, conversationBaseStatusDto.userId)
                .map { }
    }


    fun checkIsStaffService(organizationId: Int, userId: Long): Mono<ConversationStatus> {
        return findByUserId(organizationId, userId)
                .filter { it.interaction == 0 }
                .flatMap { cs ->
                    staffStatusService.assignmentCustomer(StaffChangeStatusDto(
                            cs.organizationId,
                            cs.staffId,
                            cs.userId
                    ))
                            .map { cs }
                }
                .switchIfEmpty(
                        // 如果没有分配成功, 就新建一个会话
                        statusMono.doOnSuccess {
                            it.staffId = null
                        }
                )
                .map {
                    CustomerInStaffServiceStatusDto.fromCustomerStatus(it)
                }
    }
}