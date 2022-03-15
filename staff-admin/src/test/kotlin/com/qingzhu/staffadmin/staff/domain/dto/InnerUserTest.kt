package com.qingzhu.staffadmin.staff.domain.dto

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import com.qingzhu.staffadmin.staff.mapper.DtoMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class InnerUserTest {

    @Test
    fun testMapping() {
        val staff = Staff(
            username = "admin",
            // 123456
            password = getBCryptPasswordEncoder().encode("123456"),
            role = StaffAuthority.ROLE_ADMIN,
            staffGroupId = 1,
            realName = "新之助",
            nickName = "蜡笔小新",
            avatar = null,
        ).also { it.organizationId = 9491 }
        var innerUser = DtoMapper.mapper.mapToInner(staff)
        println(innerUser)
        // 默认方法修改密码为 123456
        innerUser = DtoMapper.mapper.mapToInnerWithPassword(staff)
        println(innerUser)
        assertEquals("123456", innerUser.password)
        assertEquals(staff.username, innerUser.username)
    }
}