package com.qingzhu.staffadmin.properties.service

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.staffadmin.config.ReactorRedisCache
import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.repository.ReactivePropertiesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.reactive.awaitSingleOrNull
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
            .switchIfEmpty(Mono.just(Properties(null, key, null, null).also { it.organizationId = organizationId }))

        return reactorRedisCache.cache("prop:$organizationId:$key", result) { JsonUtils.fromJson(it) }
    }

    fun getAllProperties(organizationId: Int): IO<Mono<String>> {
        return IO.fx {
            reactorRedisCache.cache(
                "prop:$organizationId",
                createMapFromProperties(propertiesRepository.findAllByOrganizationId(organizationId))
            ) { it }
        }
    }

    suspend fun saveAll(properties: Iterable<Properties>): Flow<Properties> {
        val ids = properties.map {
            propertiesRepository.updateValueById(it.id!!, it.value!!).awaitSingleOrNull()
            it.id
        }
        val saved = propertiesRepository.findAllById(ids).asFlow()
        val savedList = saved.toList()
        val keys = savedList.map { "prop::${it.key}" }.plus("prop:all")
        reactorRedisCache.removeKey(*keys.toTypedArray()).awaitSingle()
        return saved
    }

}