package com.qingzhu.dispatcher.customer.repo.dao

import com.qingzhu.dispatcher.customer.domain.entity.Comment
import org.springframework.data.cassandra.core.mapping.MapId
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : ReactiveCassandraRepository<Comment, MapId> {
}