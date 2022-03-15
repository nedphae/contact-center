package com.qingzhu.imaccess.domain.view

import com.qingzhu.common.message.Header
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import org.junit.jupiter.api.Test
import java.time.Instant

internal class WebSocketResponseTest {

    @Test
    fun testJSON() {
        val response = WebSocketResponse(
            Header("test", "test"),
            200, MessageResponse(1L, Instant.now())
        )
        val str = response.toJson()
        println(str)
        val obj = JsonUtils.objectMapper.readValue(str, response.javaClass)
        println(obj)
    }
}