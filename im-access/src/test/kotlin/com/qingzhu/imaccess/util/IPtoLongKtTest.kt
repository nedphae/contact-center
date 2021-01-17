package com.qingzhu.imaccess.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class IPtoLongKtTest {

    @Test
    fun testIP2Int() {
        val ip = "192.168.0.101"
        val num = convertIP2Long(ip)
        println(num)
        assertEquals(3232235621L, num)
    }
}