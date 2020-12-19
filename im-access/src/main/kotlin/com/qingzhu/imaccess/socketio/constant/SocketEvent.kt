package com.qingzhu.imaccess.socketio.constant

/**
 * socket io 侦听事件
 */
object SocketEvent {
    const val register = "register"
    const val turnToStaff = "turnToStaff"

    object Message {
        const val send = "msg/send"
        const val sync = "msg/sync"
    }
}