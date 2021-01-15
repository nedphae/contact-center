package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.relational.core.mapping.Table as RTable
import javax.persistence.*

/**
 * 技能组分类
 */
@Entity
@Table(indexes = [Index(columnList = "organizationId"),
    Index(columnList = "catalogue")])
@RTable
data class ShuntClass(
        // 公司id
        val organizationId: Int,
        // 分类名称
        val className: String,
        // 上级分类
        val catalogue: Long
) : AbstractAuditingEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}