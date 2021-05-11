package com.qingzhu.imaccess.domain.query

import com.qingzhu.imaccess.domain.constant.OnlineStatus

/**
 * 客服配置
 */
data class StaffConfig(
    /** 在线状态 */
    var onlineStatus: OnlineStatus = OnlineStatus.ONLINE,
    /** 公司id */
    var organizationId: Int? = null,
    /** 客服id */
    var staffId: Long? = null,
    /** 角色种类 */
    var role: String? = null,
)