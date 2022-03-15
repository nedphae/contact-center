package com.qingzhu.staffadmin.staff.domain.query

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.common.util.ValidationUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.server.ServerWebInputException

internal class StaffQueryTest {
    @Test
    fun testValidate() {
        val query = StaffQuery(
            null,
            9491,
            "admin",
            "test",
            StaffAuthority.ROLE_STAFF,
            1,
            "test",
            "test",
            null,
        )
        assertThrows(ServerWebInputException::class.java) {
            ValidationUtils.validate(query)
        }
    }
}