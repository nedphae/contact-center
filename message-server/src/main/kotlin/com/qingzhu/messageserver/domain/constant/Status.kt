package com.qingzhu.messageserver.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class OnlineStatus {
    OFFLINE,
    ONLINE,
    BUSY,
    AWAY
}
