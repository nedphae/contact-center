package com.qingzhu.imaccess.domain.value

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.util.toJson
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

internal class ContentTest {

    @Test
    fun testJson() {
        val content = Content(MessageType.TEXT)
        val str = content.toJson()
        println(str)
        println(CreatorType.STAFF.name.toLowerCase())
        assertTrue(str.contains("1"))
    }

    @Test
    fun testEx() {
        var i = 0
        Flux.just(1)
            .map {
                println("map1: $it")
                it + 1
            }
            .map {
                println("map2: $it")
                it + 1
                // throw RuntimeException("map error")
            }
            .cache()
            .map {
                println("map3: $it")
                it + 1
                throw RuntimeException("map error")
            }
            .retry(2)
            .subscribe({
                println("subscribe: $it")
                throw RuntimeException("subscribe")
            }) {
                System.err.println(it.message)
            }
    }
}