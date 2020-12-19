package com.qingzhu.messageserver.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
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