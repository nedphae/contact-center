package com.qingzhu.messageserver.repository

import com.qingzhu.messageserver.domain.entity.ChatMessageKey
import com.qingzhu.messageserver.domain.entity.ChatMessagePO
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChatMessagePORepository : ReactiveCassandraRepository<ChatMessagePO, ChatMessageKey> {
    fun findAllByChatMessageKey_OrganizationIdAndChatMessageKey_OwnerIdAndChatMessageKey_SeqIdBefore(
        organizationId: Int, ownerId: String, id: Long, pageRequest: Pageable
    ): Mono<Slice<ChatMessagePO>>
}