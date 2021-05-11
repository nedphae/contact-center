package com.qingzhu.dispatcher.domain.entity

data class WeightInfo(
    /** 公司id */
    val organizationId: Int,
    // 客服id
    /** 每个客服只能保存一个状态 */
    val staffId: Long,
    var weight: Int,
    var max: Int,
    var current: Int
) {
    var effectiveWeight = weight

    var currentWeight = 0
}