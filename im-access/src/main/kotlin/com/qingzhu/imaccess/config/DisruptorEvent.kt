package com.qingzhu.imaccess.config

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.imaccess.domain.dto.ConversationStatusDto
import com.qingzhu.imaccess.domain.view.UpdateMessage

data class DisruptorEvent(val name: String = "message") {
    lateinit var type: EventType<*>
}

sealed class EventType<T> {
    abstract fun deserializeToPair(): T
    data class Msg(val message: String) : EventType<UpdateMessage>() {
        override fun deserializeToPair(): UpdateMessage {
            return JsonUtils.fromJson(this.message)
        }
    }

    data class Conv(val conv: String) : EventType<ConversationStatusDto>() {
        override fun deserializeToPair(): ConversationStatusDto {
            return JsonUtils.fromJson(this.conv)
        }
    }

    data class None(val channel: String) : EventType<Exception>() {
        override fun deserializeToPair(): Exception {
            throw RuntimeException("未知的消息类型：$channel")
        }
    }
}