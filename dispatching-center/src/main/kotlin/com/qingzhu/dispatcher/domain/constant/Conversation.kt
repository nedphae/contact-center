package com.qingzhu.dispatcher.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * 来源类型
 * web/ios/android/wx(微信)/wx_ma(微信小程序)/wb(微博)/open(开放接口)
 */
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
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CreatorType {
    // 系统
    SYS,

    // 工作人员
    STAFF,

    // 客户
    CUSTOMER,

    // 群聊
    GROUP
}