package com.qingzhu.dispatcher.customer.service

import com.qingzhu.dispatcher.customer.domain.entity.Comment
import com.qingzhu.dispatcher.customer.domain.query.CommentQuery
import com.qingzhu.dispatcher.customer.repo.dao.CommentRepository
import org.springframework.data.cassandra.core.ReactiveCassandraTemplate
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val cassandraTemplate: ReactiveCassandraTemplate,
) {
    fun findComment(commentQuery: CommentQuery): Mono<Slice<Comment>> {
        return cassandraTemplate.slice(commentQuery.buildSearchQuery(), Comment::class.java)
    }

    fun saveComment(commentList: Flux<Comment>): Flux<Comment> {
        return commentRepository.saveAll(commentList)
    }
}