package com.qingzhu.imaccess.domain.view

import com.qingzhu.imaccess.domain.value.Message
import java.time.LocalDateTime

/**
 * 返回消息 id
 */
data class MessageResponse(
        val seqId: Long,
        val createdAt: LocalDateTime
) {
    companion object {
        fun fromMessage(message: Message) = MessageResponse(message.seqId, message.createdAt)
    }
}