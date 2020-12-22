package com.qingzhu.dispatcher.domain.dto

import com.qingzhu.common.message.getConversationSnowFlake
import com.qingzhu.dispatcher.domain.constant.*
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

data class ConversationStatusDto(
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
        val fromType: FromType,
        // 0=客服正常会话  1=机器人会话
        val interaction: Int,
        val vipLevel: Int?,
        var isStaffInvited: Boolean = false,
        val beginner: CreatorType = CreatorType.CUSTOMER
) {
    var inQueueTime: Long = 0
    var relatedId: Long? = null
    var relatedType: RelatedType = RelatedType.NO
    var cType: ConversationType = ConversationType.NORMAL
    var visitRange: Long = 0
    lateinit var transferType: TransferType
    var humanTransferSessionId: Long = 0
    var transferFromStaffName: String? = null
    var transferFromGroup: String? = null
    var transferRemarks: String? = null

    // 生成 会话id
    val id: Long = getConversationSnowFlake().getNextSequenceId()

    // 会话开始时间
    val startTime: LocalDateTime = LocalDateTime.now()

    companion object {
        fun fromStaffAndCustomer(staffDto: StaffDto,
                                 customerDispatcherDto: CustomerDispatcherDto,
                                 interaction: Int): ConversationStatusDto {
            return ConversationStatusDto(
                    organizationId = staffDto.organizationId,
                    staffId = staffDto.staffId,
                    userId = customerDispatcherDto.userId,
                    nickName = staffDto.nickName,
                    fromGroupId = staffDto.groupId,
                    fromShuntId = customerDispatcherDto.shuntId,
                    fromIp = customerDispatcherDto.ip,
                    fromPage = customerDispatcherDto.referrer,
                    fromTitle = customerDispatcherDto.title,
                    fromType = customerDispatcherDto.fromType,
                    interaction = interaction,
                    vipLevel = customerDispatcherDto.vipLevel
            )
        }
    }
}