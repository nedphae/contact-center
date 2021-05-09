package com.qingzhu.imaccess.socketio.constant

/**
 * socket io 侦听事件
 */
object SocketEvent {
    const val register = "status/register"

    object IO {
        const val closed = "io/close"
    }

    object Message {
        const val send = "msg/send"
        const val sync = "msg/sync"
        const val assign = "assign"
    }
}