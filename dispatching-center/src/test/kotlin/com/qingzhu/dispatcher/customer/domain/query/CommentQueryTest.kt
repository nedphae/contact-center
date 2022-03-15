package com.qingzhu.dispatcher.customer.domain.query

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.format.DateTimeFormatter

internal class CommentQueryTest {
    @Test
    fun testToLong() {
        assertEquals(null, "test".toLongOrNull())
    }
    @Test
    fun testDateFormat() {
        val now = Instant.now()
        println(DateTimeFormatter.ISO_INSTANT.format(now))
    }
}