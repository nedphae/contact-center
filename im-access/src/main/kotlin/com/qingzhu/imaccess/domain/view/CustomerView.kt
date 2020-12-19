package com.qingzhu.imaccess.domain.view

data class CustomerView(
        val organizationId: Int,
        val userId: Long? = null,
        val uid: String,
        val name: String?,
        val email: String?,
        val mobile: String?,
        val vipLevel: Int?,
        val title: String?,
        val referrer: String?
)