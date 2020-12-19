package com.qingzhu.messageserver.domain.value

/**
 * 已读消息回执
 */
data class ReadMessage(
        val userId: String,
        val messageId: Long
) {
}