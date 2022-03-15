package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.message.getConversationSnowFlake
import com.qingzhu.imaccess.domain.constant.*
import java.time.Instant

/**
 * 关联到客户状态
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConversationStatusDto(
    /** 会话id 唯一 雪花 */
    val id: Long = getConversationSnowFlake().getNextSequenceId(),
    /** 公司id */
    val organizationId: Int,
    // 冗余部分数据
    /** 会话来自分流组ID */
    val fromShuntId: Long,
    /** 会话来自客服组ID */
    var fromGroupId: Long,
    /** 访客来源ip */
    val fromIp: String,
    /** 来源页 */
    val fromPage: String?,
    /** 来源页标题 */
    val fromTitle: String?,
    /** 来源类型 */
    val fromType: FromType,
    /** 列队时间 */
    val inQueueTime: Long = 0,
    /** 0=机器人会话, 1=客服正常会话 */
    val interaction: Int,
    /** 0：正常会话.1(2)：离线留言，3：排队超时 */
    val convType: ConversationType = ConversationType.NORMAL,
    /** 客服id */
    val staffId: Long,
    /** 客服实名 **/
    val realName: String,
    /** 客服名字 或为 "机器人" */
    var nickName: String,
    /** 会话开始时间 */
    val startTime: Instant = Instant.now(),
    /** 客户id */
    val userId: Long,
    /** vip 层级 0=非VIP用户 */
    val vipLevel: Int?,
    /** 与上一次来访的时间差 <=0则忽略 */
    var visitRange: Long = 0,
    /** 转人工类型 */
    var transferType: TransferType? = null,
    /** 转接来源的会话ID,0代表无转接会话 */
    val humanTransferSessionId: Long = 0,
    /** 转接来源分流客服名称 */
    val transferFromStaffName: String? = null,
    /** 转接来源分流客服组名称 */
    var transferFromGroup: String? = null,
    /** 转接来源备注 */
    var transferRemarks: String? = null,
    /** 客服是否邀请会话
     *
     * true 代表客服邀请会话
     *
     * false :代表非客服邀请会话 */
    val isStaffInvited: Boolean = false,
    /** 会话发起方  1：访客，2：客服 */
    val beginner: CreatorType = CreatorType.CUSTOMER,
    /** 关联会话id */
    var relatedId: Long? = null,
    /** 关联会话类型 */
    var relatedType: RelatedType = RelatedType.NO,
    /** 会话分类信息 "xx"， 必须用字符串，关联查询被删除就失效了 */
    var category: String? = null,
    /** 会话咨询分类明细 "xx/xx" */
    var categoryDetail: String? = null,
    /** 会话关闭原因 */
    var closeReason: CloseReason? = null,
    /** 结束时间 */
    var endTime: Instant? = null,
    /** 用户评价内容 */
    var evaluate: Evaluate? = null,
    /** 客服首次响应的时间戳 */
    var staffFirstReplyTime: Instant? = null,
    /** 客服首次响应时长(访客首条消息与客服首次回复消息的时间间隔) */
    var firstReplyCost: Long = 0,
    /** 置顶时长 */
    var stickDuration: Long = 0,
    /** 会话备注 */
    var remarks: String? = null,
    /** 客服标记的解决状态 */
    var status: SolveStatus? = null,
    /** 对话回合数 */
    var roundNumber: Int = 0,
    /** 访客首条消息时间 */
    var clientFirstMessageTime: Instant? = null,
    /** 客服平均响应时长 */
    var avgRespDuration: Long? = 0,
    /** 是否有效会话 */
    var isValid: Int? = 0,
    /** 客服消息数 */
    var staffMessageCount: Int = 0,
    /** 用户消息数 */
    var userMessageCount: Int = 0,
    /** 留言处理时间,若会话不是留言则返回0 */
    var treatedTime: Int = 0,
    //客服是否邀评  0：邀评；1：主动评价
    var isEvaluationInvited: Boolean? = null,
    /** 会话中止方  1：访客，2：客服，3：系统 */
    var terminator: CreatorType? = null,
)

/**
 * 会话评价
 */
class Evaluate(
    /** 评价模型 */
    var evaluationType: Int,
    // evaluationType:(evaluation)=>  2:(100满意1不满意);
    // 3(100满意50一般1不满意); 5(100非常满意75满意50一般25不满意1非常不满意)
    /** 否则未评价 */
    var evaluation: Int,
    /** 评价内容 */
    var evaluationRemark: String,
    /** 用户标记的解决状态，0=未选择 1=已解决 2=未解决 */
    var userResolvedStatus: Int
)