package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.common.message.getChatMessageSnowFlake
import java.util.*

class MySearchHit : SearchHit<Conversation>()

class SearchHitPage : RestResponsePage<MySearchHit>()

/**
 * 会话信息
 */
data class Conversation(
    /** 会话id 唯一 雪花 */
    val id: Long,
    /** 公司id */
    val organizationId: Int,
    /** 冗余部分数据
     * 会话来自分流组ID */
    val fromShuntId: Long,
    val fromShuntName: String?,
    /** 会话来自客服组ID */
    var fromGroupId: Long,
    var fromGroupName: String?,
    /** 访客来源ip */
    val fromIp: String,
    /** 来源页 */
    val fromPage: String?,
    /** 来源页标题 */
    val fromTitle: String?,
    /** 来源类型 */
    var fromType: String?,
    /** 列队时间 */
    val inQueueTime: Long,
    /** 0=机器人会话, 1=客服正常会话 */
    val interaction: Int,
    /** 0：正常会话.1(2)：离线留言，3：排队超时 */
    var convType: String?,
    /** 客服id */
    val staffId: Long,
    /** 客服名称 **/
    val realName: String,
    /** 客服名字 或为 "机器人" */
    var nickName: String,
    /** 会话开始时间 */
    val startTime: Double,
    /** 客户id */
    val userId: Long,
    /** 客户名称 */
    var userName: String?,
    /** vip 层级 0=非VIP用户 */
    val vipLevel: Int?,
    /** 与上一次来访的时间差 <=0则忽略 */
    val visitRange: Long = 0,
    /** 转人工类型 */
    var transferType: String?,
    /** 转接来源的会话ID,0代表无转接会话 */
    val humanTransferSessionId: Long = 0,
    /** 转接来源分流客服名称 */
    val transferFromStaffName: String? = null,
    /** 转接来源分流客服组名称 */
    var transferFromGroup: String? = null,
    /** 转接来源备注 */
    var transferRemarks: String? = null,
    /** 客服是否邀请会话 true 代表客服邀请会话，false :代表非客服邀请会话 */
    val isStaffInvited: Boolean = false,
    /** 会话发起方  1：访客，2：客服 */
    var beginner: String,
    /** 关联会话id */
    var relatedId: Long? = null,
    /** 关联会话类型 */
    var relatedType: String,
    /** 会话分类信息 "xx"， 必须用字符串，关联查询被删除就失效了 */
    var category: String? = null,
    /** 会话咨询分类明细 "xx/xx" */
    var categoryDetail: String? = null,
    /** 会话关闭原因 */
    var closeReason: String? = null,
    /** 结束时间 */
    var endTime: Double? = null,
    /** 用户评价内容 */
    var evaluate: Evaluate? = null,
    /** 客服首次响应的时间戳 */
    var staffFirstReplyTime: Double? = null,
    /** 客服首次响应时长(访客首条消息与客服首次回复消息的时间间隔) */
    var firstReplyCost: Long = 0,
    /** 置顶时长 */
    var stickDuration: Long = 0,
    /** 会话备注 */
    var remarks: String? = null,
    /** 客服标记的解决状态 */
    var status: String? = null,
    /** 对话回合数 */
    var roundNumber: Int = 0,
    /** 访客首条消息时间 */
    var clientFirstMessageTime: Double? = null,
    /** 客服平均响应时长 */
    var avgRespDuration: Long? = 0,
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
    var terminator: String? = null,

    var chatMessages: List<Message>? = null,
)

class MessagePage : RestResponsePage<Message>()

data class Message(
    /** 公司id */
    var organizationId: Int? = null,
    val uuid: String = UUID.randomUUID().toString(),
    val seqId: Long = getChatMessageSnowFlake().getNextSequenceId(),
    val createdAt: Double,
    /** 会话id */
    val conversationId: Long,
    /** 消息来源 (服务器设置) */
    var from: Long? = null,
    /** 消息送至 */
    var to: Long? = null,
    /** 消息类型 接收者类型 */
    val type: Int,
    /** 创建者类型 */
    val creatorType: Int,
    /** 内容 */
    val content: Content,
    /** 昵称 */
    val nickName: String? = null
)

class Evaluate(
    /** 评价模型 */
    var evaluationType: Int,
    /**
     * evaluationType:(evaluation)=>
     * 2:(100满意1不满意);
     * 3(100满意50一般1不满意); 5(100非常满意75满意50一般25不满意1非常不满意)
     * 否则未评价
     */
    var evaluation: Int,
    /** 评价内容 */
    var evaluationRemark: String,
    /** 用户标记的解决状态，0=未选择 1=已解决 2=未解决 */
    var userResolvedStatus: Int
)

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Content(
    /** Content type:
     *
     * text, picture, file, system message(text)
     */
    val contentType: String,
    /** sys 消息类型 **/
    val sysCode: Int? = null,
    /** text */
    var textContent: TextContent? = null,
    /** picture */
    var photoContent: PhotoContent? = null,
    /** file */
    var attachments: Attachments? = null

) {
    data class TextContent(
        val text: String
    )

    data class PhotoContent(
        val mediaId: String,
        val filename: String,
        val picSize: Int,
        /** pic type */
        val type: String
    )

    data class Attachments(
        val mediaId: String,
        val filename: String,
        val size: Int,
        /** Display different icons according to the type */
        val type: String,
    )
}