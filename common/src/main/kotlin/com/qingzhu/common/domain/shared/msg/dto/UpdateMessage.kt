package com.qingzhu.common.domain.shared.msg.dto

import com.qingzhu.common.domain.shared.msg.value.Message

data class UpdateMessage(
    /**
     * 上一条接受的消息ID，或者事件序列ID
     * 用以检查是否漏收了消息
     */
    val pts: Long,
    val message: Message,
    val sentClientId: String?,
    /**
     * 上一条消息之间更新中的事件数
     * 这里类似于 telegram 的 qts (秘密聊天，事件不打包成组)
     * 每次都实时传输聊天消息
     */
    val ptsCount: Long = 1,
)
