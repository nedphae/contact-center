package com.qingzhu.messageserver.service

import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.CloseReason
import com.qingzhu.messageserver.domain.constant.FromType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class Test(
    val id: Int,
    val type: FromType,
    val reason: CloseReason,
)

class EnumTest {
    @Test
    fun testEnum() {
        val test = Test(1, FromType.WX_MA, CloseReason.ADMIN_TAKE_OVER)
        println(test.toJson())
    }

    @Test
    fun testDate() {
        val time = Instant.now()
        val localDateTime = LocalDateTime.ofInstant(time, ZoneId.systemDefault())
        val parse = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
        assertEquals(parse, time)
    }
}