package com.qingzhu.staffadmin.properties.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.relational.core.mapping.Table as RTable
import javax.persistence.*

@Entity
@Table(indexes = [Index(columnList = "key", unique = true), Index(columnList = "label")])
@RTable("properties")
data class Properties(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int?,
        // 公司id
        val organizationId: Int,
        @Column(columnDefinition = "varchar(50)")
        val key: String,
        @Column(columnDefinition = "varchar(500)")
        var value: String?,
        @Column(columnDefinition = "varchar(50)")
        var label: String?,
        // 是否启用
        var available: Boolean = true
) : AbstractAuditingEntity()