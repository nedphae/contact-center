package com.qingzhu.dispatcher.customer.controller

import com.qingzhu.common.util.awaitGetOrganizationId
import com.qingzhu.dispatcher.customer.domain.entity.Comment
import com.qingzhu.dispatcher.customer.domain.query.CommentQuery
import com.qingzhu.dispatcher.customer.service.CommentService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.*

@RestController
class CommentHandler(private val commentService: CommentService) {
    suspend fun findComment(sr: ServerRequest): ServerResponse {
        val (orgId) = sr.awaitGetOrganizationId()
        return sr.bodyToMono<CommentQuery>()
            .doOnNext { it.organizationId = orgId }
            .flatMap { ServerResponse.ok().body(commentService.findComment(it)) }
            .awaitSingle()
    }

    suspend fun saveComment(sr: ServerRequest): ServerResponse {
        return sr.bodyToFlux<Comment>()
            .transform { ServerResponse.ok().body(commentService.saveComment(it)) }
            .awaitSingle()
    }
}