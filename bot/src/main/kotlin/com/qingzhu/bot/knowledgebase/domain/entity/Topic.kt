package com.qingzhu.bot.knowledgebase.domain.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.qingzhu.common.domain.shared.msg.dto.ChatUIContent
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

data class Answer(
    val type: String,
    val content: String,
) {
    fun toChatUIMessage(): ChatUIMessage {
        val uiContent = when(type) {
            "text" -> {
                ChatUIContent(text = content)
            }
            "image" -> ChatUIContent(picUrl = content)
            else -> ChatUIContent(text = content)
        }
        return ChatUIMessage(
            type,
            content = uiContent
        )
    }
}

/**
 * 知识库主题 vo
 */
@Document(indexName = "knowledge_base_topic", shards = 1, replicas = 0)
data class Topic(
    /** 机构 ID **/
    var organizationId: Int? = null,
    /** 所属知识库ID **/
    val knowledgeBaseId: Long,
    /** 问题，使用ik分词器查询和索引 */
    @Field(type = FieldType.Text/*, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"*/)
    var question: String,
    /** 问题的md5 */
    @Field(type = FieldType.Keyword)
    var md5: String?,
    /** 问题的对外答案，如果是相似问题，可以设置为空 */
    @Field(type = FieldType.Object)
    var answer: List<Answer>?,
    /** 问题的对内答案 */
    @Field(type = FieldType.Text)
    var innerAnswer: String?,
    /** 问题的来源,0:用户手动添加,1:寒暄库,2:文件导入 */
    var fromType: Int = 0,
    /** 问题类型,1:标准问题,2:相似问题 */
    var type: Int,
    /** 相似问题(type=10)对应的标准问题id */
    @Field(type = FieldType.Keyword)
    var refId: String?,
    /** 关联的问题id列表 */
    @Field(type = FieldType.Keyword)
    var connectIds: List<String>?,
    /** 是否有效标记位 */
    var enabled: Boolean,
    /** 问题的有效时间 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var effectiveTime: Instant?,
    /** 有效期结束 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var failureTime: Instant?,
    /** 知识点所属分类 */
    var categoryId: Long,
) {
    @Id
    var id: String? = null

    /** 问题答案类型，0只有对外答案，1只有对内答案，2同时有对内和对外答案，null 相似问题，无答案 */
    var faqType: Int? = null

    init {
        if (answer != null && innerAnswer != null) {
            faqType = 2
        } else if (innerAnswer != null) {
            faqType = 1
        } else if (answer != null) {
            faqType = 0
        }
    }

    @CreatedDate
    @JsonIgnore
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    val createdDate: Instant? = Instant.now()

    @LastModifiedDate
    @JsonIgnore
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var lastModifiedDate: Instant? = Instant.now()
}