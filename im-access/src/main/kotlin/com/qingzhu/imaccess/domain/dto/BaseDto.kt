package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.broker.KafkaBroker

open class BaseDto {
    // 所处服务器
    val hashKey: String = KafkaBroker.hashKey
}