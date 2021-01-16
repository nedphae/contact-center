package com.qingzhu.staffadmin.staff.domain.dto

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.staffadmin.staff.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class InnerUserTest {

    @Test
    fun testMapping() {
        val staff = Staff(
                organizationId = 9491,
                username = "admin",
                // 123456
                password = getBCryptPasswordEncoder().encode("123456"),
                role = StaffAuthority.ROLE_ADMIN,
                staffGroupId = 1,
                realName = "新之助",
                nickName = "蜡笔小新"
        )
        val innerUser = InnerUser.mapper.mapToInner(staff)
        println(innerUser)
        assertEquals(staff.username, innerUser.username)
    }
}