package com.qingzhu.messageserver.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class ReadyStatus {
    UNREADY,
    READY
}

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class OnlineStatus {
    OFFLINE,
    ONLINE
}

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class BusyStatus {
    IDLE,
    BUSY
}

enum class StaffRole {
    // 管理员
    ADMIN,

    // 客服
    STAFF,

    // 组长
    LEADER,

    // 质检
    QA
}