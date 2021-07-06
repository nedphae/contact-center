package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractStaffEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

//NOTE: 也可以使用 Cassandra 替换数据库
@Table
data class QuickReply(
    @Id
    var id: Long? = null,
    var groupId: Long? = null,
    var title: String,
    var content: String,
    var personal: Boolean,
) : AbstractStaffEntity()


@Table
data class QuickReplyGroup(
    @Id
    var id: Long? = null,
    val groupName: String,
    var personal: Boolean,
) : AbstractStaffEntity()