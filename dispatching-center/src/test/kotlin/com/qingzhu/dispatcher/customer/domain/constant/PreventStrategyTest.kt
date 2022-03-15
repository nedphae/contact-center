package com.qingzhu.dispatcher.customer.domain.constant

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PreventStrategyTest {
    @Test
    fun testEnum() {
        println(PreventStrategy.UID)
        assertEquals("UID", PreventStrategy.UID.toString())
        println(PreventStrategy.valueOf("UID"))
        assertEquals(PreventStrategy.UID, PreventStrategy.valueOf("UID"))
    }
}