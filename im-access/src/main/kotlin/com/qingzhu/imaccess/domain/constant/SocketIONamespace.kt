package com.qingzhu.imaccess.domain.constant

enum class SocketIONamespace(val namespace: String) {
    CUSTOMER("/im/customer"),
    STAFF("/im/staff"),
    CHAT_BOT("/im/chat-bot")
}