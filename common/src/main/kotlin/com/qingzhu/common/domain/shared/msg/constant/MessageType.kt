package com.qingzhu.common.domain.shared.msg.constant

import com.fasterxml.jackson.annotation.JsonFormat

enum class MessageType {
    // 系统消息
    SYS,

    // 文本消息
    TEXT,

    // 图片消息
    IMAGE,

    // 语音消息
    VOICE,

    // 文件消息
    FILE,

    // 链接消息
    LINK
}

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class SysCode {
    // 更新列队
    UPDATE_QUEUE,
    // 分配列队
    ASSIGN,
    // 无答案
    NO_ANSWER,
    // 修改在线状态
    ONLINE_STATUS_CHANGED,
    // 会话结束
    CONV_END,
    // 自动回复
    AUTO_REPLY,
}

/**
 * 参考钉钉设计的创建者类型
 * 不同于钉钉的 普通消息/OA消息 区分
 * 这里区分不同的使用者类型 (客服/客户)
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CreatorType(val type: String) {
    // 系统
    SYS("系统"),

    // 工作人员
    STAFF("客服"),

    // 客户
    CUSTOMER("客户"),

    // 群聊
    GROUP("群聊"),
}