package com.qingzhu.imaccess.domain.dto

import com.qingzhu.imaccess.broker.RedisBroker

open class BaseDto {
    // 所处服务器
    val redisHashKey: Long = RedisBroker.hashKey
}