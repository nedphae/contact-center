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
    var organizationId: Int? = null,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    var staffId: Long? = null,
    var groupId: Long? = null,
    var title: String,
    var content: String,
    var personal: Boolean,
) : AbstractAuditingEntity()


@Table
data class QuickReplyGroup(
    @Id
    var id: Long? = null,
    /** 公司id */
    var organizationId: Int? = null,
    // 配置的客服 (每个客服可以有多个配置)
    /** @ManyToOne */
    var staffId: Long? = null,
    val groupName: String,
    var personal: Boolean,
) : AbstractAuditingEntity()