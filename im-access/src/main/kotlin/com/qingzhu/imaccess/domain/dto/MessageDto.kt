package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.broker.KafkaBroker
import com.qingzhu.imaccess.domain.value.Message

data class MessageDto(
    /** 发送的服务器名称 */
    val accessServer: String = KafkaBroker.accessServer,
    val message: Message
)
