package com.qingzhu.staffadmin.config

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import org.reactivestreams.Publisher
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.cache.CacheFlux
import reactor.cache.CacheMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal
import java.time.Duration

@Configuration
class ReactorRedisCache(
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val valueOperations = redisTemplate.opsForValue()
    private val listOperations = redisTemplate.opsForList()
    private val timeout = Duration.ofHours(12)

    fun <T> cache(key: String, flux: Flux<T>, fromJson: (String) -> T): Flux<T> {
        return CacheFlux
            .lookup({ k ->
                listOperations.range(k, 0, -1)
                    .map { fromJson(it) }
                    .materialize()
                    .collectList()
            }, key)
            .onCacheMissResume(flux)
            .andWriteWith { t, u ->
                Flux
                    .fromIterable(u)
                    .dematerialize<T>()
                    .map { JsonUtils.toJson(it) }
                    .collectList()
                    .flatMap {
                        listOperations.leftPushAll(t, it)
                        redisTemplate.expire(t, timeout)
                    }
                    .flatMap {
                        Mono.empty()
                    }
            }
    }

    fun <T: Any> cache(key: String, mono: Mono<T>, fromJson: (String) -> T): Mono<T> {
        return CacheMono
            .lookup({ k ->
                valueOperations[k]
                    .map { fromJson(it) }
                    .map { Signal.next(it) }
            }, key)
            .onCacheMissResume(mono)
            .andWriteWith { t, u ->
                valueOperations.set(t, u.get().toJson(), timeout)
                    .flatMap { Mono.empty() }
            }
    }

    fun removeKey(vararg keys: String) = redisTemplate.delete(*keys)

    fun removeKey(keys: Publisher<String>) = redisTemplate.delete(keys)
}