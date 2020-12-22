package com.qingzhu.dispatcher.domain.dto

data class StaffDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val groupId: Long,
        val username: String,
        val realName: String,
        val nickName: String,
        val gender: Int,
        val personalizedSignature: String,
        val staffType: Int
)