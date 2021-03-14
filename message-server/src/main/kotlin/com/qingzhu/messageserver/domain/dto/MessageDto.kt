package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.value.Message

data class MessageDto(
    /** 发送消息的客户端id */
    val client: String,
    val message: Message
)
