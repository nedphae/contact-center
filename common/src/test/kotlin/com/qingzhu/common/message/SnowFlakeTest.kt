package com.qingzhu.common.message

import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

internal class SnowFlakeTest {

    @Test
    fun getNextSequenceId() {
        println(System.currentTimeMillis())
        val snowFlake = SnowFlake(0, 0)
        val long = snowFlake.getNextSequenceId()
        printfWithFormat(long, long.toDouble())
        println()
        val maxDouble = Double.MAX_VALUE
        printfWithFormat(Long.MAX_VALUE, maxDouble)
        println()
        System.out.printf("%f - %d", Long.MAX_VALUE.toDouble(), maxDouble.toLong())
        assertTrue(long < snowFlake.getNextSequenceId())
        println()
        for (i in 0..11) {
            println("$i 位")
            val intercept = Long.MAX_VALUE shr i
            printfWithFormat(intercept, intercept.toDouble())
            println()
        }
    }

    private fun printfWithFormat(long: Long, double: Double) {
        System.out.printf("%d - %f", long, double)
    }

    @Test
    fun testDate() {
        println(Date(0L))
        println(Date(1480166465631L))
        println(System.currentTimeMillis())
    }
}