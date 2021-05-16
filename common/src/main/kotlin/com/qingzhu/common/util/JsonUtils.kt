package com.qingzhu.common.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.math.BigDecimal

object JsonUtils {
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    fun <T> toJson(data: T): String {
        return objectMapper.writeValueAsString(data)
    }

    inline fun <reified T> fromJson(content: String): T {
        // String 类型就不解析了
        if (T::class == String::class) {
            return content as T
        }
        return objectMapper.readValue(content)
    }
}

class BigDecimalSerializer : JsonSerializer<BigDecimal>() {
    override fun serialize(value: BigDecimal?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.times(100.toBigDecimal()).toString())
    }
}

fun <T> T.toJson(): String {
    return JsonUtils.toJson(this)
}