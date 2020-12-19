package com.qingzhu.dispatcher.domain.dto

data class StaffDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val username: String,
        val realName: String,
        val nickName: String,
        val gender: Int,
        val personalizedSignature: String
) {
    fun toStaffViewWithUserId(userId: Long): ConversationView {
        return ConversationView(
                organizationId, staffId, userId, nickName
        )
    }
}