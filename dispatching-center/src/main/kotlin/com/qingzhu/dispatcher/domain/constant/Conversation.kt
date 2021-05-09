package com.qingzhu.dispatcher.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * 0-客服关闭；1-访客离开页面失效关闭；2-访客离开超时关闭；3-访客申请其他客服关闭；
 * 4-网络差客服掉线关闭；5-客服转接关闭；6-管理员接管关闭；7-访客主动关闭；
 * 8-系统关闭，静默超时关闭；9-系统关闭，静默转接关闭；10-访客未说话；
 * 11-访客排队超时清队列；12-访客放弃排队；13-客服离线清队列；
 * 16-系统关闭会话；其他-机器人转人工。
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CloseReason(val reason: String) {
    STAFF_CLOSE("客服关闭"),
    USER_LEFT("访客离开页面失效关闭"),
    USER_TIME_OUT("访客离开超时关闭"),
    USER_OTHER_STAFF("访客申请其他客服关闭"),
    USER_NET_ERROR("网络差客服掉线关闭"),
    TRANSLATE("客服转接关闭"),
    ADMIN_TAKE_OVER("管理员接管"),
    USER_CLOSE("访客主动关闭"),
    SYS_CLOSE("系统关闭，静默超时关闭"),
    SYS_TRAN("系统关闭，静默转接关闭"),
    USER_SILENT("访客未说话"),
    USER_QUEUE_TIMEOUT("访客排队超时清队列"),
    USER_QUEUE_LEFT("访客放弃排队"),
    STAFF_OFFLINE("客服离线清队列"),
    BOT_TO_STAFF("机器人转人工"),
}

/**
 * 来源类型
 * web/ios/android/wx(微信)/wx_ma(微信小程序)/wb(微博)/open(开放接口)
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class FromType(val type: String) {
    WEB("web"),
    IOS("ios"),
    ANDROID("android"),
    WX("微信"),
    WX_MA("微信小程序"),
    WB("微博"),
    OPEN("开放接口"),
}

/**
 * 关联会话类型
 * 0=无关联 1=机器人会话转接人工 2=历史会话发起 3=客服间转接 4=被接管
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class RelatedType(val type: String) {
    NO("无关联"),
    FROM_BOT("机器人会话转接人工"),
    FROM_HISTORY("历史会话发起"),
    FROM_STAFF("客服间转接"),
    BE_TAKEN_OVER("被接管")
}

/**
 * 转人工类型：主动转人工、关键词转人工、
 * 回复引导转人工、拦截词转人工、连续未知转人工、
 * 差评转人工、情绪识别转人工、图片转人工
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class TransferType(val type: String) {
    INITIATIVE("主动转人工"),
    KEYWORD("关键词转人工"),
    REPLY("回复引导转人工"),
    INTERCEPT_WORD("拦截词转人工"),
    CONTINUOUS_UNKNOWN("连续未知转人工"),
    NEGATIVE_RATINGS("差评转人工"),
    EMOTION_RECOGNITION("情绪识别转人工"),
    PIC("图片转人工"),
}

/**
 * 会话类型
 * 0：正常会话.1(2)：离线留言，3：排队超时
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class ConversationType(val type: String) {
    NORMAL("正常会话"),
    OFFLINE_COMMENT("离线留言"),
    QUEUE_TIMEOUT("排队超时"),
}

/**
 * 参考钉钉设计的创建者类型
 * 不同于钉钉的 普通消息/OA消息 区分
 * 这里区分不同的使用者类型 (客服/客户)
 */

/**
 * staff flag resolution status
 * 0：unsolved、1：solved、2：solving
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class SolveStatus(val type: String) {
    UNSOLVED("unsolved"),
    SOLVED("solved"),
    SOLVING("solving")
}