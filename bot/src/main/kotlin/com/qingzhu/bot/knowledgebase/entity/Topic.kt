package com.qingzhu.bot.knowledgebase.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime
import java.util.*

/**
 * 知识库主题 vo
 */
@Document(indexName = "knowledgebase-topic", shards = 1, replicas = 0)
data class Topic(
		/** 机构 ID **/
		val organizationId: Int,
		/** 所属知识库ID **/
		val knowledgeBaseId: Long,
		/** 问题，使用ik分词器查询和索引 */
		@Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
		var question: String,
		/** 问题的md5 */
		var md5: String,
		/** 问题的对外答案，如果是相似问题，可以设置为空 */
		var answer: String?,
		/** 问题的对内答案 */
		var innerAnswer: String?,
		/** 问题答案类型，0只有对外答案，1只有对内答案，2同时有对内和对外答案，3 相似问题，无答案 */
		var faqType: Int?,
		/** 问题的来源,0:用户手动添加,1:寒暄库,2:文件导入 */
		var fromType: Int,
		/** 语音播报回复 */
		var updateTime: LocalDateTime,
		/** 问题类型,1:标准问题,10:相似问题 */
		var type: Int,
		/** 相似问题(type=10)对应的标准问题id */
		var refId: Long?,
		/** 关联的问题id列表 */
		var connectIds: List<Long>?,
		/** 是否有效标记位 */
		var enabled: Boolean,
		/** 问题的失效时间 */
		var effectiveTime: Date,
		/** 有效期结束 */
		var failureTime: Date,
		/** 知识点所属分类 */
		var categoryId: Long,
) : AbstractAuditingEntity() {
	@Id
	var id: Long? = null
}