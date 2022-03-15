package com.qingzhu.common.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.convertValue
import java.math.BigDecimal

object JsonUtils {
    val objectMapper: ObjectMapper = jacksonObjectMapper().registerModule(JavaTimeModule()).registerModule(Jdk8Module())

    fun <T> toJson(data: T): String {
        return if (data is String) {
             data
        } else objectMapper.writeValueAsString(data)
    }

    inline fun <reified T> fromJson(content: String): T {
        return objectMapper.readValue(content)
    }
}

class BigDecimalSerializer : JsonSerializer<BigDecimal>() {
    override fun serialize(value: BigDecimal?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeString(value?.times(100.toBigDecimal()).toString())
    }
}

class RawStringSerializer : JsonDeserializer<String>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): String {
        val tree = p.codec.readTree<TreeNode>(p)
        return tree.toString()
    }
}

fun <T> T.toJson(): String {
    return JsonUtils.toJson(this)
}

fun <T: Any> T.toMap(): Map<String, Any?> {
    return JsonUtils.objectMapper.convertValue(this)
}
