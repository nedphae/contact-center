package com.qingzhu.bot.knowledgebase.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 知识库分类管理
 */
@Table
data class TopicCategory(
    /** 分类名称 */
    var name: String,
    /** 所属知识库ID **/
    val knowledgeBaseId: Long,
    /** 上级分类 */
    val pid: Long?,
) : AbstractAuditingEntity() {
    @Id
    var id: Long? = null
}
