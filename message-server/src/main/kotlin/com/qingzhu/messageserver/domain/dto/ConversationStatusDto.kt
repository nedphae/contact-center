package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.entity.ConversationStatus

data class ConversationBaseStatusDto(
        val id: Long,
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val userId: Long,
        val nickName: String,
        val interaction: Int
) {
    companion object {
        fun fromConversationStatus(conversationStatus: ConversationStatus): ConversationBaseStatusDto {
            return ConversationBaseStatusDto(
                    id = conversationStatus.id,
                    organizationId = conversationStatus.organizationId,
                    staffId = conversationStatus.staffId,
                    userId = conversationStatus.userId,
                    nickName = conversationStatus.nickName,
                    interaction = conversationStatus.interaction
            )
        }
    }
}

data class ConversationStatusDto(
        val id: Long,
        val organizationId: Int,
        val staffId: Long,
        val userId: Long
) {
}