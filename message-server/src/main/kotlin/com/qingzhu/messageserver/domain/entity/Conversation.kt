package com.qingzhu.messageserver.domain.entity

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.Instant

/**
 * ES 实体
 */
@Document(indexName = "message_conv", shards = 1, replicas = 0)
data class Conversation(
    /** 会话id 唯一 雪花 */
    @Id
    val id: Long,
    /** 公司id */
    val organizationId: Int,
    /** 冗余部分数据
     * 会话来自分流组ID */
    val fromShuntId: Long,
    @Field(type = FieldType.Keyword)
    val fromShuntName: String?,
    /** 会话来自客服组ID */
    var fromGroupId: Long,
    @Field(type = FieldType.Keyword)
    var fromGroupName: String?,
    /** 访客来源ip */
    @Field(type = FieldType.Ip)
    val fromIp: String,
    /** 来源页 */
    @Field(type = FieldType.Keyword)
    val fromPage: String?,
    /** 来源页标题 */
    @Field(type = FieldType.Keyword)
    val fromTitle: String?,
    /** 来源类型 */
    @Field(type = FieldType.Keyword)
    var fromType: String?,
    /** 列队时间 */
    val inQueueTime: Long,
    /** 0=机器人会话, 1=客服正常会话 */
    val interaction: Int,
    /** 0：正常会话.1(2)：离线留言，3：排队超时 */
    @Field(type = FieldType.Keyword)
    var convType: String?,
    /** 客服id */
    val staffId: Long,
    /** 客服名称 **/
    @Field(type = FieldType.Keyword)
    val realName: String,
    /** 客服名字 或为 "机器人" */
    @Field(type = FieldType.Keyword)
    var nickName: String,
    /** 会话开始时间 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    val startTime: Instant,
    /** 客户id */
    val userId: Long,
    /** 客户名称 */
    var userName: String?,
    /** vip 层级 0=非VIP用户 */
    val vipLevel: Int?,
    /** 与上一次来访的时间差 <=0则忽略 */
    val visitRange: Long = 0,
    /** 转人工类型 */
    @Field(type = FieldType.Keyword)
    var transferType: String?,
    /** 转接来源的会话ID,0代表无转接会话 */
    val humanTransferSessionId: Long = 0,
    /** 转接来源分流客服名称 */
    @Field(type = FieldType.Keyword)
    val transferFromStaffName: String? = null,
    /** 转接来源分流客服组名称 */
    @Field(type = FieldType.Keyword)
    var transferFromGroup: String? = null,
    /** 转接来源备注 */
    @Field(type = FieldType.Keyword)
    var transferRemarks: String? = null,
    /** 客服是否邀请会话 true 代表客服邀请会话，false :代表非客服邀请会话 */
    val isStaffInvited: Boolean = false,
    /** 会话发起方  1：访客，2：客服 */
    @Field(type = FieldType.Keyword)
    var beginner: String,
    /** 关联会话id */
    var relatedId: Long? = null,
    /** 关联会话类型 */
    @Field(type = FieldType.Keyword)
    var relatedType: String,
    /** 会话分类信息 "xx"， 必须用字符串，关联查询被删除就失效了 */
    @Field(type = FieldType.Keyword)
    var category: String? = null,
    /** 会话咨询分类明细 "xx/xx" */
    @Field(type = FieldType.Keyword)
    var categoryDetail: String? = null,
    /** 会话关闭原因 */
    @Field(type = FieldType.Keyword)
    var closeReason: String? = null,
    /** 结束时间 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var endTime: Instant? = null,
    /** 用户评价内容 */
    @Field(type = FieldType.Object)
    var evaluate: Evaluate? = null,
    /** 客服首次响应的时间戳 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var staffFirstReplyTime: Instant? = null,
    /** 客服首次响应时长(访客首条消息与客服首次回复消息的时间间隔) */
    var firstReplyCost: Long = 0,
    /** 置顶时长 */
    var stickDuration: Long = 0,
    /** 会话备注 */
    // TODO 添加 ik 分词器
    @Field(type = FieldType.Text/*, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"*/)
    var remarks: String? = null,
    /** 客服标记的解决状态 */
    @Field(type = FieldType.Keyword)
    var status: String? = null,
    /** 对话回合数 */
    var roundNumber: Int = 0,
    /** 访客首条消息时间 */
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    var clientFirstMessageTime: Instant? = null,
    /** 客服平均响应时长 */
    var avgRespDuration: Long = 0,
    /** 是否有效会话 */
    var isValid: Int? = 0,
    /** 客服消息数 */
    var staffMessageCount: Int = 0,
    /** 用户消息数 */
    var userMessageCount: Int = 0,
    /** 总消息数 **/
    var totalMessageCount: Int = 0,
    /** 留言处理时间,若会话不是留言则返回0 */
    var treatedTime: Int = 0,
    //客服是否邀评  0：邀评；1：主动评价
    var isEvaluationInvited: Boolean? = null,
    /** 会话中止方  1：访客，2：客服，3：系统 */
    @Field(type = FieldType.Keyword)
    var terminator: String? = null,

    @Field(type = FieldType.Nested)
    var chatMessages: List<ChatMessage>? = null,
) {
    fun setChatMessageAndStatistics(chatMessages: List<ChatMessage>?) {
        this.chatMessages = chatMessages
        if (chatMessages != null) {
            this.totalMessageCount = chatMessages.size
            this.userMessageCount = chatMessages.filter { it.creatorType == CreatorType.CUSTOMER }.size
            this.staffMessageCount = chatMessages.filter { it.creatorType == CreatorType.STAFF }.size
            this.isValid = if (CreatorType.STAFF == chatMessages.first().creatorType) 1 else 0
            var roundStart: CreatorType? = null
            var roundEnd: CreatorType? = null
            chatMessages.forEach {
                if (roundStart == null && it.creatorType != roundEnd){
                    roundStart = it.creatorType
                } else {
                    if (roundStart != it.creatorType) {
                        roundEnd = it.creatorType
                        roundStart = null
                        this.roundNumber += 1
                    }
                }
            }

        }
    }
}
