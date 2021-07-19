package com.qingzhu.messageserver.repository.search

import com.qingzhu.messageserver.domain.entity.Conversation
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface ConversationRepository : ReactiveSortingRepository<Conversation, String> {
    fun findFirstByOrganizationIdAndUserIdOrderByStartTimeDesc(organizationId: Int, userId: Long): Mono<Conversation>
}