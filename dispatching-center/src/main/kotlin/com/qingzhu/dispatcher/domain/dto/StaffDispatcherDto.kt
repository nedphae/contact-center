package com.qingzhu.dispatcher.domain.dto

data class StaffDispatcherDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        val staffId: Long,
        // 不同接待组的优先级
        var priorityOfGroup: Map<Long, Int>,
        // 最大接待数量
        var maxServiceCount: Int,
        // 当前接待量
        var currentServiceCount: Int
) {
}