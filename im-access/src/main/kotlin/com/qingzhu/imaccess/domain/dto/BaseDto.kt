package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.broker.KafkaBroker

open class BaseDto {
    /** Which server i`m in */
    val accessServer: String = KafkaBroker.accessServer
}