package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.domain.query.StaffConfig

data class StaffChangeStatusDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        // 每个客服只能保存一个状态
        val staffId: Long
)

/**
 * 设置客服状态(初始状态)
 */
data class StaffStatusDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        // 每个客服只能保存一个状态
        val staffId: Long,
        // 角色种类
        var role: String,
        // 所处接待组
        var receptionistGroup: List<Long>,
        // 不同接待组的优先级
        var priorityOfGroup: Map<Long, Int>,
        // 在线状态
        var onlineStatus: Int = 1,
        // 就绪状态
        var readyStatus: Int = 1,
        // 繁忙状态
        var busyStatus: Int = 1,
        // 最大接待数量
        var maxServiceCount: Int = 8
) : BaseDto() {
    companion object {
        fun fromStaffConfigAndStaff(staffConfig: StaffConfig, receptionistGroupDto: ReceptionistGroupDto): StaffStatusDto {
            return StaffStatusDto(
                    organizationId = staffConfig.organizationId!!,
                    staffId = staffConfig.staffId!!,
                    role = staffConfig.role,
                    receptionistGroup = receptionistGroupDto.receptionistGroup,
                    priorityOfGroup = receptionistGroupDto.priorityOfGroup,
                    onlineStatus = staffConfig.onlineStatus,
                    readyStatus = staffConfig.readyStatus,
                    busyStatus = staffConfig.busyStatus,
                    maxServiceCount = receptionistGroupDto.maxServiceCount
            )
        }
    }
}