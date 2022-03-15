package com.qingzhu.staffadmin.properties.domain.entity

import com.qingzhu.common.domain.AbstractStaffEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("properties")
data class Properties(
    @Id
    val id: Int?,
    val key: String?,
    var value: String?,
    var label: String?,
    /** 是否启用 */
    var available: Boolean = true,
    /** 是系统/个人配置 */
    var personal: Boolean = false,
) : AbstractStaffEntity()