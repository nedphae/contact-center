package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.entity.StaffStatus

data class StaffDispatcherDto(
    /** 公司id */
    val organizationId: Int,
    /** 客服id */
    val staffId: Long,
    /** 不同接待组的优先级 */
    var priorityOfShunt: Pair<Long, Int?>,
    /** 最大接待数量 */
    var maxServiceCount: Int,
    /** 当前接待量 */
    var currentServiceCount: Int,
    val staffType: Int
) {
    companion object {
        fun fromStaffStatusAndShuntId(shuntId: Long, staffStatus: StaffStatus): StaffDispatcherDto {
            return StaffDispatcherDto(
                organizationId = staffStatus.organizationId,
                staffId = staffStatus.staffId,
                priorityOfShunt = shuntId to staffStatus.priorityOfShuntMap[shuntId],
                maxServiceCount = staffStatus.maxServiceCount,
                currentServiceCount = staffStatus.currentServiceCount,
                staffType = staffStatus.staffType
            )
        }
    }
}