package com.qingzhu.bot.knowledgebase.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 机器人 配置
 */
@Table
data class BotConfig(
        /** 机构 ID **/
        val organizationId: Int,
        @Id
        var id: Long? = null,
        /** 机器人ID */
        var botId: Long,
        /** 机器人 与 知识库的映射 */
        // one to one
        var knowledgeBaseId: Long,
) : AbstractAuditingEntity()
