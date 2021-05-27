package com.qingzhu.messageserver.service

import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.CloseReason
import com.qingzhu.messageserver.domain.constant.FromType
import org.junit.jupiter.api.Test

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
}