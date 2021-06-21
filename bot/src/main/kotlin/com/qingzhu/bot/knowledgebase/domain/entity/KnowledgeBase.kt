package com.qingzhu.bot.knowledgebase.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class KnowledgeBase(
    /** 机构 ID **/
    val organizationId: Int,
    var name: String,
    var description: String?,
) : AbstractAuditingEntity() {
    @Id
    var id: Long? = null
}
