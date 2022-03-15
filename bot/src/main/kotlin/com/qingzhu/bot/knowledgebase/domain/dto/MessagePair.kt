package com.qingzhu.bot.knowledgebase.domain.dto

import com.qingzhu.common.domain.shared.msg.value.Message

data class MessagePair(
    val questionMessage: Message,
    val answerMessage: List<Message>,
)
