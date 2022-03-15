package com.qingzhu.messageserver.repository.search

import com.qingzhu.messageserver.domain.entity.Conversation
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface ConversationRepository : ReactiveSortingRepository<Conversation, Long> {
    fun findFirstByOrganizationIdAndUserIdAndInteractionOrderByStartTimeDesc(organizationId: Int, userId: Long, interaction: Int = 1): Mono<Conversation>
}