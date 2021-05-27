package com.qingzhu.messageserver.repository.search

import com.qingzhu.messageserver.domain.entity.Conversation
import org.springframework.data.repository.reactive.ReactiveSortingRepository

interface ConversationRepository : ReactiveSortingRepository<Conversation, String> {
}