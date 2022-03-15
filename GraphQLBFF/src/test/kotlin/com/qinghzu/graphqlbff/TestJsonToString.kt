package com.qinghzu.graphqlbff

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.RawStringSerializer
import com.qingzhu.common.util.toJson
import org.junit.jupiter.api.Test

class TestJsonToString {

    data class JsonTest(
        val id: String,
        val name: String,
        val sub: SubObj,
        val subList: List<SubObj>,
    )

    data class SubObj(
        val id: String,
        val name: String,
    )

    open class JsonTestWithString(
        val id: String? = null,
        val name: String? = null,
        @field:JsonDeserialize(using = RawStringSerializer::class)
        val sub: String? = null,
        @field:JsonDeserialize(using = RawStringSerializer::class)
        val subList: String? = null,
    )

    class JsonTestWithStringOpen : JsonTestWithString()

    @Test
    fun testJsonToString() {
        val test = JsonTest("1", "fuck json", SubObj("2", "fuck json too"), listOf(SubObj("3", "fuck json too")))
        val testStr = test.toJson()
        println(testStr)
        val obj = JsonUtils.fromJson<JsonTestWithStringOpen>(testStr)
        println(obj)
    }
}