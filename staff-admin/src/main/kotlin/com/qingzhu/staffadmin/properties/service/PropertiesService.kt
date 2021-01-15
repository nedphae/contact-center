package com.qingzhu.staffadmin.properties.service

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.repository.PropertiesRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun createMapFromProperties(properties: Flux<Properties>): Mono<String> {
    // 使用 JavaPropsFactory 格式化成配置文件格式
    val propsReader = ObjectMapper(JavaPropsFactory())
    return properties.collectList()
            .map { list ->
                list.joinToString(separator = System.lineSeparator()) {
                    """${it.key}.value:${it.value}
|${it.key}.id:${it.id}
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
        private val propertiesRepository: PropertiesRepository,
        private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    /**
     * 系统内部使用的配置项目接口
     */
    @Cacheable("properties", key = "args")
    fun findDistinctTopByKey(organizationId: Int, key: String): Mono<Properties> {
        val result = propertiesRepository.findDistinctTopByKey(key)
        return result.switchIfEmpty(Mono.just(Properties(null, organizationId, key, null, null)))
    }

    fun getAllProperties(): IO<Mono<String>> {
        return IO.fx {
            val properties = propertiesRepository.findAll()
            createMapFromProperties(properties)
        }
    }

    fun saveAll(properties: Iterable<Properties>): IO<Flux<Properties>> {
        return IO.fx {
            val result = propertiesRepository.saveAll(properties).cache()
            redisTemplate.delete(result.map { "properties::${it.key}" })
            result
        }
    }

}