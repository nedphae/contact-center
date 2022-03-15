package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 客服分组
 */

@Table
data class StaffGroup(
    @Id
    var id: Long? = null,
    /** 部门名称 */
    var groupName: String,
) : AbstractAuditingEntity()