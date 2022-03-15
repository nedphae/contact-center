package com.qingzhu.common.util

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class JsonUtilsTest {
    @Test
    fun testJsonFromString() {
        val int = JsonUtils.fromJson<Int>("1")
        assertEquals(1, int)
        assertEquals(""""OK"""", "OK".toJson())
        val str = JsonUtils.fromJson<String>("OK".toJson())
        assertEquals("OK", str)
    }

    @Test
    fun testEnum() {
        println(StaffAuthority.ROLE_ADMIN.name)
        println(StaffAuthority.valueOf(StaffAuthority.ROLE_ADMIN.name))
    }

}