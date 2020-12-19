package com.qingzhu.imaccess.domain.query

/**
 * 客服配置
 */
data class StaffConfig(
        // 角色种类
        var role: String,
        // 在线状态
        var onlineStatus: Int = 1,
        // 就绪状态
        var readyStatus: Int = 1,
        // 繁忙状态
        var busyStatus: Int = 1
) {
    // 公司id
    var organizationId: Int? = null

    // 客服id
    var staffId: Long? = null
}