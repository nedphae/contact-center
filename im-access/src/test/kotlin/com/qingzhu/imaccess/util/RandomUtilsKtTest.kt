package com.qingzhu.imaccess.util

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class RandomUtilsKtTest {

    @Test
    fun testInt() {
        val num = getRandomInt()
        println(num)
        assertTrue(num < 1000000000)
    }

}