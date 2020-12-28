package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.messageserver.domain.constant.*
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import java.time.LocalDateTime

data class ConversationEndDto(
        val organizationId: Int,
        val id: Long,
        // 默认用户是机器人转人工
        val closeReason: CloseReason,
        val endTime: LocalDateTime,
        // 机器人会话全部设置为有效
        val isValid: Int,
        var relatedId: Long?,
        // 关联类型 为转接到人工
        val relatedType: RelatedType,
        // 客户转接到客服，设置机器人会话为系统关闭
        val terminator: CreatorType
)

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

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConversationStatusDto(
        val id: Long,
        val startTime: LocalDateTime,
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val userId: Long,
        val nickName: String,
        var fromGroupId: Long,
        val fromShuntId: Long,
        var fromIp: String,
        var fromPage: String?,
        val fromTitle: String?,
        var fromType: FromType,
        var inQueueTime: Long,
        // 0=客服正常会话  1=机器人会话
        val interaction: Int,
        var relatedId: Long?,
        val relatedType: RelatedType = RelatedType.NO,
        val cType: ConversationType,
        val vipLevel: Int?,
        val visitRange: Long,
        val transferType: TransferType,
        val humanTransferSessionId: Long = 0,
        val transferFromStaffName: String? = null,
        var transferFromGroup: String? = null,
        var transferRemarks: String? = null,
        val isStaffInvited: Boolean = false,
        val beginner: CreatorType
) {
    fun toConversationStatus(): ConversationStatus {
        return ConversationStatus(
                id = this.id,
                organizationId = this.organizationId,
                fromShuntId = this.fromShuntId,
                fromGroupId = this.fromGroupId,
                fromIp = this.fromIp,
                fromPage = this.fromPage,
                fromTitle = this.fromTitle,
                fromType = this.fromType,
                inQueueTime = this.inQueueTime,
                interaction = this.interaction,
                cType = this.cType,
                staffId = this.staffId,
                startTime = this.startTime,
                userId = this.userId,
                vipLevel = this.vipLevel,
                visitRange = this.visitRange,
                transferType = this.transferType,
                humanTransferSessionId = this.humanTransferSessionId,
                transferFromStaffName = this.transferFromStaffName,
                transferFromGroup = this.transferFromGroup,
                transferRemarks = this.transferRemarks,
                isStaffInvited = this.isStaffInvited,
                beginner = this.beginner
        ).also {
            it.relatedId = this.relatedId
            it.relatedType = this.relatedType
        }
    }
}