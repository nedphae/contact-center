package com.qingzhu.messageserver.repository

import com.qingzhu.messageserver.domain.entity.ChatMessageKey
import com.qingzhu.messageserver.domain.entity.ChatMessagePO
import org.springframework.data.cassandra.repository.Query
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ChatMessagePORepository : ReactiveCassandraRepository<ChatMessagePO, ChatMessageKey> {
    @Query("select * from chat_msg where organization_id = ?0 and owner_id = ?1 and seq_id < ?2 order by seq_id desc")
    fun findAllBySeqId(
        organizationId: Int,
        ownerId: String,
        seqId: Long,
        pageRequest: Pageable,
    ): Mono<Slice<ChatMessagePO>>

    @Query("select count(*) from chat_msg where organization_id = ?0 and owner_id = ?1 and seq_id < ?2")
    fun countBySeqId(
        organizationId: Int,
        ownerId: String,
        seqId: Long,
    ): Mono<Long>

    @Query("select count(*) from chat_msg where organization_id = ?0 and owner_id = ?1")
    fun countAll(
        organizationId: Int,
        ownerId: String,
    ): Mono<Long>
}