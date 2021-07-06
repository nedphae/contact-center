package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractStaffEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class StaffConfig(
    @Id
    var id: Long? = null,
    /** 接待组 id */
    val shuntId: Long,
    /** 配置优先级 */
    var priority: Int
) : AbstractStaffEntity()