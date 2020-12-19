package com.qingzhu.staffadmin.staff.domain.dto


data class InnerUser(
        val organizationId: Int,
        val id: Long,
        val username: String,
        var password: String,
        var role: String
)