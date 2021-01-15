package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.relational.core.mapping.Table as RTable
import javax.persistence.*

/**
 * 接待组 aka 技能组
 */
@Entity
@Table(indexes = [Index(columnList = "organizationId"),
    Index(columnList = "shuntClassId")])
@RTable
data class Shunt(
        // 公司id
        val organizationId: Int,
        // 接待组 名称
        val name: String,
        // 接待组所属分类
        // @ManyToOne
        val shuntClassId: Long,
        // 接待组范围代码
        val code: String
) : AbstractAuditingEntity() {
    @Id
    var id: Long? = null
}