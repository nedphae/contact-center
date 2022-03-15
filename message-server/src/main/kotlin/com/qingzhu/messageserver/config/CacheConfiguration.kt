package com.qingzhu.messageserver.config

import com.hazelcast.core.HazelcastInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@Configuration
@EnableRedisRepositories
class RedisConfiguration

@Configuration
class HazelcastConfiguration {

    @Autowired
    fun setHazelcastInstance(@Qualifier("hazelcastInstance") hazelcastInstance: HazelcastInstance) {
        CacheManager.hazelcastInstance = hazelcastInstance
    }
}

object CacheManager {
    lateinit var hazelcastInstance: HazelcastInstance
}