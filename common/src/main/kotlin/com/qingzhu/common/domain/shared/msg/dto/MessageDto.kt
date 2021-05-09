package com.qingzhu.common.domain.shared.msg.dto

import com.qingzhu.common.domain.shared.msg.value.Message

data class MessageDto(
    /** 发送消息的客户端id */
    val client: String?,
    val message: Message
) {
    constructor(message: Message) : this(null, message)
}
