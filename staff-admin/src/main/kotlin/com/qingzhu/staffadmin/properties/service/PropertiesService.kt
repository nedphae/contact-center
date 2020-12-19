package com.qingzhu.staffadmin.properties.service

import arrow.fx.IO
import arrow.fx.extensions.fx
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory
import com.qingzhu.staffadmin.properties.domain.entity.Properties
import com.qingzhu.staffadmin.properties.repo.dao.jpa.PropertiesRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

fun createMapFromProperties(properties: List<Properties>): String {
    // 使用 JavaPropsFactory 格式化成配置文件格式
    val propsReader = ObjectMapper(JavaPropsFactory())
    val propertiesValue = properties.joinToString(separator = System.lineSeparator()) {
        """${it.key}.value:${it.value}
        |${it.key}.id:${it.id}
    """.trimMargin()
    }
    val obj = propsReader.readValue(propertiesValue, Any::class.java)
    val jsonWriter = ObjectMapper()
    return jsonWriter.writeValueAsString(obj)
}

@Service
class PropertiesService(
        private val propertiesRepository: PropertiesRepository,
        private val redisTemplate: RedisTemplate<String, String>
) {
    /**
     * 系统内部使用的配置项目接口
     */
    @Cacheable("properties", key = "args")
    fun findDistinctTopByKey(organizationId: Int, key: String): Properties {
        val result = propertiesRepository.findDistinctTopByKey(key)
        return result.orElse(Properties(null, organizationId, key, null, null))
    }

    fun getAllProperties(): IO<String> {
        return IO.fx {
            val properties = propertiesRepository.findAll()
            createMapFromProperties(properties)
        }
    }

    fun saveAll(properties: Iterable<Properties>): IO<List<Properties>> {
        return IO.fx {
            val result = propertiesRepository.saveAll(properties)
            val keyList = result.map { "properties::${it.key}" }.toList()
            redisTemplate.delete(keyList)
            result
        }
    }

}