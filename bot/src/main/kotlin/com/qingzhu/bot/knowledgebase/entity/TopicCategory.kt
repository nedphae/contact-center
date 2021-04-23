package com.qingzhu.bot.knowledgebase.entity

import org.springframework.data.elasticsearch.annotations.Document

/**
 * 知识库分类管理
 */
@Document(indexName = "knowledgebase-topic-category")
data class TopicCategory(
        /** 机构 ID **/
        val organizationId: Int,
        val id: Long,
        /** 分类名称 */
        var name: String,
        /** 上级分类 */
        val pid: Long,
)
