package com.qingzhu.dispatcher.customer.service

import com.qingzhu.common.domain.shared.RangeQuery
import com.qingzhu.dispatcher.DispatcherApplicationTests
import com.qingzhu.dispatcher.customer.domain.entity.Comment
import com.qingzhu.dispatcher.customer.domain.query.CommentQuery
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.time.Instant

internal class CommentServiceTest : DispatcherApplicationTests() {
    @Autowired
    private lateinit var commentService: CommentService
    
    val now: Instant = Instant.now()

    @Test
    @Order(2)
    fun testComment() {
        val comment = Comment(9491, now,1, 100, "test", "测试留言", null, "test@test.test", "测试留言")
        val saveResult = commentService.saveComment(Flux.just(comment))
        StepVerifier
            .create(saveResult)
            .assertNext {
                assertEquals(comment, it)
            }
            .verifyComplete()
    }

    @Test
    @Order(1)
    fun testQuery() {
        val query = CommentQuery(
            9491,
            timeRange = RangeQuery("2021-09-03T03:40:33.521", now.toEpochMilli().toString())
        )
        val findResult = commentService.findComment(query)
        StepVerifier
            .create(findResult.map { it.content.first() })
            .assertNext {
                assertEquals("test", it.uid)
            }
            .verifyComplete()
    }
}