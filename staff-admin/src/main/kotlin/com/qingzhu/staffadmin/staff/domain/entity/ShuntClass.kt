package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 技能组分类
 */

@Table
data class ShuntClass(
    @Id
    var id: Long? = null,
    /** 分类名称 */
    val className: String,
    /** 上级分类 */
    val catalogue: Long
) : AbstractAuditingEntity()