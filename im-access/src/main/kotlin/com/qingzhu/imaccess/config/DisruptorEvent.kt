package com.qingzhu.imaccess.config

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.imaccess.domain.value.Message

data class DisruptorEvent(val name: String = "message") {
    lateinit var type: EventType<*>
}

sealed class EventType<T> {
    abstract fun deserializeToPair(): T
    data class Msg(val message: String) : EventType<Message>() {
        override fun deserializeToPair(): Message {
            return JsonUtils.fromJson(this.message)
        }
    }

    data class None(val channel: String) : EventType<Exception>() {
        override fun deserializeToPair(): Exception {
            throw RuntimeException("未知的消息类型：$channel")
        }
    }
}