package com.qingzhu.bot.knowledgebase.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class KnowledgeBase(
    var name: String,
    var description: String?,
) : AbstractAuditingEntity() {
    @Id
    var id: Long? = null
}
