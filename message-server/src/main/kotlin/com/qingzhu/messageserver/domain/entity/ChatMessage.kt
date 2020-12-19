package com.qingzhu.messageserver.domain.entity

import com.qingzhu.common.constant.NoArg


/**
 * 聊天消息实体类
 */
@NoArg
data class ChatMessage(
        // 服务器生成的有序ID
        val seqId: Long,
        // 客户端生成聊天消息id
        var msgId: Long
) {
}