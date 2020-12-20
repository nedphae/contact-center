package com.qingzhu.dispatcher.domain.dto

import com.qingzhu.common.message.getConversationSnowFlake
import java.time.LocalDateTime


/**
 * 返回给用户的客服的公开信息
 */
data class ConversationView(
        val id: Long,
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val userId: Long,
        val nickName: String,
        // 0=客服正常会话  1=机器人会话
        val interaction: Int
) {
}

data class ConversationDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        val userId: Long,
        val nickName: String,
        var fromGroupId: Long,
        var fromGroup: String,
        var fromIp: String,
        var fromPage: String?,
        var fromStaff: String?,
        var fromType: String,
        var inQueueTime: Long,
        // 0=客服正常会话  1=机器人会话
        val interaction: Int,
        var relatedId: Long?,
        var relatedType: Int?,
) {
    // 生成 会话id
    val id: Long = getConversationSnowFlake().getNextSequenceId()
    // 会话开始时间
    val startTime: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromStaffAndCustomer(staffDto:StaffDto): ConversationDto {
            return ConversationDto(
                    organizationId = staffDto.organizationId,
                    staffId = staffDto.staffId,
                    userId = ,
                    nickName = staffDto.nickName
            )
        }
    }
}