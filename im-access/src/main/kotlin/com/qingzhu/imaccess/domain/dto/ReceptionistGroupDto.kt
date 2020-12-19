package com.qingzhu.imaccess.domain.dto

data class ReceptionistGroupDto(
        // 公司id
        val organizationId: Int,
        // 客服id
        // 每个客服只能保存一个状态
        val staffId: Long,
        // 所处接待组
        var receptionistGroup: List<Long>,
        // 不同接待组的优先级
        var priorityOfGroup: Map<Long, Int>,
        // 最大接待数量
        var maxServiceCount: Int = 8
) {
}