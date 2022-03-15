package com.qingzhu.bot.knowledgebase.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 机器人 配置
 */
@Table
data class BotConfig(
    /** 机器人ID, 对应 staff Id */
    var botId: Long,
    /** 机器人 与 知识库的映射 */
    // one to one
    var knowledgeBaseId: Long,
    /** 没有找到答案时的回复 */
    var noAnswerReply: String = "抱歉，没有找到您想要的答案",
) : AbstractAuditingEntity() {
    @Id
    var id: Long? = null

    fun getAnswer(): Answer {
        return Answer("text", noAnswerReply)
    }
}
