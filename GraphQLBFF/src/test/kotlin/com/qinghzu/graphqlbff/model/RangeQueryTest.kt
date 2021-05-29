package com.qinghzu.graphqlbff.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class RangeQueryTest {

    @Test
    fun testType() {
        val rangeQuery: RangeQuery<Int> = RangeQuery(from = null, to = null)
        val type = rangeQuery::class
    }

}