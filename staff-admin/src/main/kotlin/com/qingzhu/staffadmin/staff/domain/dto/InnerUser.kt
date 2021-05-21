package com.qingzhu.staffadmin.staff.domain.dto

data class InnerUser(
    val organizationId: Int,
    val id: Long = 0,
    val username: String,
    var password: String?,
    var role: String
)