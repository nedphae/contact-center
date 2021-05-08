package com.qingzhu.staffadmin.properties.service

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.repository.ReactivePropertiesRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.cache.CacheMono
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Signal

fun createMapFromProperties(properties: Flux<Properties>): Mono<String> {
    // 使用 JavaPropsFactory 格式化成配置文件格式
    val propsReader = ObjectMapper(JavaPropsFactory())
    return properties.collectList()
        .map { list ->
            list.joinToString(separator = System.lineSeparator()) {
                """${it.key}.value:${it.value}
                        ${it.key}.id:${it.id}
                        """.trimMargin()
            }
        }
        .map {
            val obj = propsReader.readValue(it, Any::class.java)
            val jsonWriter = ObjectMapper()
            jsonWriter.writeValueAsString(obj)
        }
}

@Service
class PropertiesService(
    private val propertiesRepository: ReactivePropertiesRepository,
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val valueOperations = redisTemplate.opsForValue()

    /**
     * 系统内部使用的配置项目接口
     */
    fun findDistinctTopByKey(organizationId: Int, key: String): Mono<Properties> {
        val result = propertiesRepository.findDistinctTopByKey(key)
            .switchIfEmpty(Mono.just(Properties(null, organizationId, key, null, null)))
        return CacheMono
            .lookup({ k ->
                valueOperations[k]
                    .map { JsonUtils.fromJson<Properties>(it) }
                    .map { Signal.next(it) }
            }, "prop:$organizationId:$key")
            .onCacheMissResume(result)
            .andWriteWith { t, u ->
                Mono.fromRunnable { valueOperations[t] = u.get().toJson() }
            }
    }

    fun getAllProperties(): IO<Mono<String>> {
        return IO.fx {
            val properties = propertiesRepository.findAll()
            val result = createMapFromProperties(properties)
            CacheMono
                .lookup({ k ->
                    valueOperations[k]
                        .map { Signal.next(it) }
                }, "prop:all")
                .onCacheMissResume(result)
                .andWriteWith { t, u ->
                    Mono.fromRunnable { valueOperations[t] = u.get().toString() }
                }
        }
    }

    fun saveAll(properties: Iterable<Properties>): IO<Flux<Properties>> {
        return IO.fx {
            val result = propertiesRepository.saveAll(properties).cache()
            redisTemplate.delete(result.map { "prop::${it.key}" })
            result
        }
    }

}