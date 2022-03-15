package com.qinghzu.graphqlbff.model

data class Answer(
    val type: String,
    val content: String,
)

data class Topic(
    var id: String? = null,
    /** 机构 ID **/
    val organizationId: Int? = null,
    /** 所属知识库ID **/
    val knowledgeBaseId: Long,
    /** 问题，使用ik分词器查询和索引 */
    var question: String,
    /** 问题的md5 */
    var md5: String?,
    /** 问题的对外答案，如果是相似问题，可以设置为空 */
    var answer: List<Answer>?,
    /** 问题的对内答案 */
    var innerAnswer: String?,
    /** 问题的来源,0:用户手动添加,1:寒暄库,2:文件导入 */
    var fromType: Int? = 0,
    /** 问题类型,1:标准问题,2:相似问题 */
    var type: Int,
    /** 相似问题(type=10)对应的标准问题id */
    var refId: String?,
    /** 关联的问题id列表 */
    var connectIds: List<String>?,
    /** 是否有效标记位 */
    var enabled: Boolean,
    /** 问题的有效时间 */
    var effectiveTime: Double?,
    /** 有效期结束 */
    var failureTime: Double?,
    /** 知识点所属分类 */
    var categoryId: Long,
    /** 问题答案类型，0只有对外答案，1只有对内答案，2同时有对内和对外答案，null 相似问题，无答案 */
    var faqType: Int? = null,
)

data class BotConfig(
    var id: Long? = null,
    /** 机构 ID **/
    val organizationId: Int? = null,
    /** 机器人ID, 对应 staff Id */
    var botId: Long,
    /** 机器人 与 知识库的映射 */
    // one to one
    var knowledgeBaseId: Long,
    /** 没有找到答案时的回复 */
    var noAnswerReply: String,
)

data class KnowledgeBase(
    var id: Long? = null,
    /** 机构 ID **/
    val organizationId: Int? = null,
    var name: String,
    var description: String?,
)

data class TopicCategory(
    var id: Long? = null,
    /** 机构 ID **/
    val organizationId: Int? = null,
    /** 分类名称 */
    var name: String,
    /** 所属知识库ID **/
    val knowledgeBaseId: Long,
    /** 上级分类 */
    val pid: Long?,
)
