package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.domain.AbstractStaffEntity
import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.messageserver.domain.constant.OnlineStatus
import com.qingzhu.messageserver.domain.entity.StaffStatus
import com.qingzhu.messageserver.mapper.StaffStatusMapper

@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffChangeStatusDto(
    /** 公司id */
    val organizationId: Int,
    /** 客服id */
    val staffId: Long,

    val userId: Long? = null,

    val clientId: String? = null,
)

data class UpdateStaffStatus(
    var onlineStatus: OnlineStatus,
): AbstractStaffEntity()
/**
 * 设置客服状态(初始状态)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class StaffStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    /** 角色种类 */
    var role: StaffAuthority,
    /** 所处接待组 */
    var shunt: Set<Long>,
    /** 客服分组 **/
    var groupId: Long,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Map<Long, Int>,
    /** 客服所处服务器名 */
    val clientAccessServer: Pair<String, String>? = null,
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 客服类型，0 表示机器人，1 表示人工。 */
    val staffType: Int,
) {
    fun toStaffStatus(): StaffStatus = StaffStatusMapper.mapper.fromDtoWithMap(this)

    fun toStaffStatus(oldStaffStatus: StaffStatus): StaffStatus {
        val newStaffStatus = this.toStaffStatus()
        newStaffStatus.clientAccessServerMap.plusAssign(oldStaffStatus.clientAccessServerMap)
        newStaffStatus.userIdList.plusAssign(oldStaffStatus.userIdList)
        return newStaffStatus
    }
}
