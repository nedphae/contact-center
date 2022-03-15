package com.qingzhu.dispatcher.config

import com.qingzhu.common.component.BaseReactorRedisCache
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate

@Configuration
class ReactorRedisCache(
    redisTemplate: ReactiveRedisTemplate<String, String>
) : BaseReactorRedisCache(redisTemplate)