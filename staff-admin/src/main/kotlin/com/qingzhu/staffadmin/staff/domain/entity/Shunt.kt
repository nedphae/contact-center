package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 接待组 aka 技能组
 */
@Table
data class Shunt(
    @Id
    var id: Long? = null,
    /** 接待组 名称 */
    val name: String,
    // 接待组所属分类
    /** @ManyToOne */
    val shuntClassId: Long,
    /** 接待组范围代码 */
    var code: String?
) : AbstractAuditingEntity()