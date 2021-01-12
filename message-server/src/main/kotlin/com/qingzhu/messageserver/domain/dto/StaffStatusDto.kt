package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.constant.StaffRole
import com.qingzhu.messageserver.domain.entity.StaffStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffChangeStatusDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,

        val userId: Long?
)

/**
 * 设置客服状态(初始状态)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffStatusDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        // 每个客服只能保存一个状态
        val staffId: Long,
        // 角色种类
        var role: StaffRole,
        // 所处接待组
        var receptionistGroup: List<Long>,
        // 不同接待组的优先级
        var priorityOfGroup: Map<Long, Int>,
        // 客服所处服务器 hash 值
        var hashKey: String? = null,
        // 在线状态
        var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
        // 最大接待数量
        var maxServiceCount: Int = 8
) {
    fun toStaffStatus(): StaffStatus = StaffStatus(
            organizationId = organizationId,
            staffId = staffId,
            role = role,
            receptionistGroup = receptionistGroup,
            priorityOfGroup = priorityOfGroup,
            hashKey = hashKey,
            maxServiceCount = maxServiceCount
    ).also {
        it.onlineStatus = onlineStatus
    }
}