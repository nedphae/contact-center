package com.qingzhu.dispatcher.domain.dto

data class StaffChangeStatusDto(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,

    val userId: Long
)