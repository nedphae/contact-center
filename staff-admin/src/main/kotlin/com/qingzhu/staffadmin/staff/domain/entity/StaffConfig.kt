package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.relational.core.mapping.Table as RTable
import javax.persistence.*

@Entity
@Table(indexes = [Index(columnList = "organizationId"),
    Index(columnList = "staffId"), Index(columnList = "shuntId")])
@RTable
data class StaffConfig(
        // 公司id
        val organizationId: Int,
        // 配置的客服 (每个客服可以有多个配置)
        // @ManyToOne
        val staffId: Long,
        // 接待组 id
        val shuntId: Long,
        // 配置优先级
        var priority: Int
) : AbstractAuditingEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(columnDefinition = "serial")
    var id: Long? = null
}