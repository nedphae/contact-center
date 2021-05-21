package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

//NOTE: 也可以使用 Cassandra 替换数据库
@Table
data class QuickReply(
    @Id
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    var groupId: Long?,
    var title: String,
    var content: String,
    var personal: Boolean,
) : AbstractAuditingEntity()


@Table
data class QuickReplyGroup(
    @Id
    var id: Long? = null,
    /** 公司id */
    val organizationId: Int,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    val staffId: Long?,
    val groupName: String,
    var personal: Boolean,
) : AbstractAuditingEntity()