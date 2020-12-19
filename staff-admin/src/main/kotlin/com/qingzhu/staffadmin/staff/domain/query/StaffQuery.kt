package com.qingzhu.staffadmin.staff.domain.query

import com.qingzhu.common.security.password.getBCryptPasswordEncoder
import com.qingzhu.staffadmin.staff.authority.StaffAuthority
import com.qingzhu.staffadmin.staff.domain.entity.Staff
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


data class StaffQuery(
        @field: NotNull(message = "公司ID不能为空")
        val organizationId: Int,
        @field: NotEmpty(message = "用户名不能为空")
        val username: String,
        @field: Length(message = "密码长度必须大于6小于100", min = 6, max = 100)
        val password: String,
        @field: Size(message = "用户角色不能为空", min = 1)
        val role: String,
        @field: NotNull(message = "用户分组不能为空")
        val groupId: Long
) {
    fun toCustomerServiceRepresentative(): Staff {
        return Staff(
                organizationId = organizationId,
                username = username,
                password = getBCryptPasswordEncoder().encode(password),
                role = StaffAuthority.valueOf(role),
                staffGroupId = groupId
        )
    }
}