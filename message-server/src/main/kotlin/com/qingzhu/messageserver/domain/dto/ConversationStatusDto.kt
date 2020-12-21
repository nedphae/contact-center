package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.constant.CreatorType
import com.qingzhu.messageserver.domain.constant.FromType
import com.qingzhu.messageserver.domain.constant.RelatedType
import com.qingzhu.messageserver.domain.constant.TransferType
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import java.time.LocalDateTime

data class ConversationBaseStatusDto(
        val id: Long,
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val userId: Long,
        val interaction: Int
) {
    companion object {
        fun fromConversationStatus(conversationStatus: ConversationStatus): ConversationBaseStatusDto {
            return ConversationBaseStatusDto(
                    id = conversationStatus.id,
                    organizationId = conversationStatus.organizationId,
                    staffId = conversationStatus.staffId,
                    userId = conversationStatus.userId,
                    interaction = conversationStatus.interaction
            )
        }
    }
}

data class ConversationStatusDto(
        val id: Long,
        val organizationId: Int,
        val fromShuntId: Long,
        val fromIp: String,
        val fromPage: String?,
        val fromTitle: String?,
        val fromType: FromType,
        val inQueueTime: Long,
        val interaction: Int,
        val relatedId: Long?,
        val relatedType: RelatedType = RelatedType.NO,
        val cType: Int,
        val staffId: Long,
        val startTime: LocalDateTime,
        val userId: Long,
        val vipLevel: Int,
        val visitRange: Long,
        val transferType: TransferType,
        val humanTransferSessionId: Long = 0,
        val transferFromStaffName: String? = null,
        var transferFromGroup: String? = null,
        var transferRemarks: String? = null,
        val isStaffInvited: Boolean = false,
        val beginner: CreatorType
) {
}