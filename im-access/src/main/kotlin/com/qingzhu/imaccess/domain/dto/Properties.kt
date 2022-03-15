package com.qingzhu.imaccess.domain.dto

import com.qingzhu.common.domain.AbstractStaffEntity

data class Properties(
    val id: Int?,
    val key: String?,
    var value: String?,
    var label: String?,
    /** 是否启用 */
    var available: Boolean = true,
    /** 是系统/个人配置 */
    var personal: Boolean = false,
) : AbstractStaffEntity()