package com.qinghzu.graphqlbff.model

import org.junit.jupiter.api.Test

internal class QueryTest {

    @Test
    fun testType() {
        val rangeQuery: RangeQuery<Int> = RangeQuery(from = null, to = null)
        val type = rangeQuery::class
    }

}