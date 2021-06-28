package com.qingzhu.staffadmin.properties.service

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.repository.ReactivePropertiesRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

fun createMapFromProperties(properties: Flux<Properties>): Mono<String> {
    // 使用 JavaPropsFactory 格式化成配置文件格式
    val propsReader = ObjectMapper(JavaPropsFactory())
    return properties.collectList()
        .map { list ->
            list.joinToString(separator = System.lineSeparator()) {
                """ ${if (it.value != null) "${it.key}.value:${it.value}" else ""}
                    ${it.key}.id:${it.id}
                    ${it.key}.label:${it.label}
                    ${it.key}.available:${it.available}
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
    private val reactorRedisCache: ReactorRedisCache,
) {

    /**
     * 系统内部使用的配置项目接口
     */
    fun findDistinctTopByKey(organizationId: Int, key: String): Mono<Properties> {
        val result = propertiesRepository.findDistinctTopByOrganizationIdAndKeyAndPersonalIsFalse(organizationId, key)
            .switchIfEmpty(Mono.just(Properties(null, organizationId, key, null, null)))

        return reactorRedisCache.cache("prop:$organizationId:$key", result) { JsonUtils.fromJson(it) }
    }

    fun getAllProperties(organizationId: Int): IO<Mono<String>> {
        return IO.fx {
            reactorRedisCache.cache("prop:$organizationId", createMapFromProperties(propertiesRepository.findAll())) { it }
        }
    }

    fun saveAll(properties: Iterable<Properties>): IO<Flux<Properties>> {
        return IO.fx {
            val result = propertiesRepository.saveAll(properties).cache()
            reactorRedisCache.removeKey(result.map { "prop::${it.key}" }.concatWith { Mono.just("prop:all") })
                .flatMapMany { result }
        }
    }

}