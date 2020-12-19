package com.qingzhu.messageserver.domain.entity

import com.qingzhu.common.constant.NoArg
import java.time.LocalDateTime

/**
 * 关联到客户状态
 */
@NoArg
data class ConversationStatus(
        // 公司id
        val organizationId: Int,
        // 会话id 唯一 雪花
        val id: Long,
        // 会话分类信息
        var category: String,
        // 会话关闭原因
        /**
         * 会话关闭原因：0-客服关闭；1-用户离开；2-系统关闭，表示用户长时间不说话，自动关闭；
         * 3-用户转接人工客服；4-客服无网络连接，系统自动关闭会话；5-会话转接；6-管理员接管；
         * 7-用户关闭会话；8-系统关闭，静默超时关闭；9-系统关闭，静默转接关闭；10-访客未说话；
         * 11-访客排队超时清队列；12-访客放弃排队；13-客服离线清队列；其他-机器人转人工。
         */
        var closeReason: Int,
        // 结束时间
        var endTime: LocalDateTime,
        // 用户评价内容
        var evaluate: Evaluate,
        // 冗余部分数据
        // 会话来自分流组ID
        var fromGroupId: Long,
        // 会话来自分流组名称
        var fromGroup: String,
        // 访客来源ip
        var fromIp: String,
        // 来源页
        var fromPage: String,
        // 来自哪个客服名 / 转接
        var fromStaff: String,
        // 来源类型 web/ios/android/wx(微信)/wx_ma(微信小程序)/wb(微博)/open(开放接口)
        var fromType: String,
        // 列队时间
        var inQueueTime: Long,
        // 0=客服正常会话  1=机器人会话
        var interaction: Int,
        // 关联会话id
        var relatedId: Long,
        // 关联会话类型
        // 0=无关联  1=从机器人转接过来 2=机器人会话转接人工 3=历史会话发起 4=客服间转接 5=被接管
        var relatedType: Int,
        // 客服id
        var staffId: Long,
        // 客服名字 或为 "机器人"
        var nickName: String,
        // 会话开始时间
        var startTime: LocalDateTime,
        // 置顶时长
        var stickDuration: Long,
        // 客户id
        var userId: Long,
        // 会话备注
        var remarks: String,
        // 客服标记的解决状态
        // 0：未解决、1：已解决、2：解决中
        var status: Int,
        // vip 层级 0=非VIP用户
        var vipLevel: Int,
        // 与上一次来访的时间差 <=0则忽略
        var visitRange: Long,
        // 转人工类型：主动转人工、关键词转人工、回复引导转人工、拦截词转人工、连续未知转人工、差评转人工、情绪识别转人工、图片转人工
        var transferRgType: Int,
        // 对话回合数
        var roundNumber: Int
) {
}