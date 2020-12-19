package com.qingzhu.staffadmin.properties.service

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class PropertiesServiceKtTest {
    @Test
    fun testProperties() {
        val properties = createMapFromProperties(listOf(Properties(1, 9491, "sys.auto-reply.test", "21", "test"),
                Properties(2, 9491, "sys.auto-reply.test1", "20", "test")))
        println(properties)
        assertEquals("""{"sys":{"auto-reply":{"test1":{"id":"2","value":"20"},"test":{"id":"1","value":"21"}}}}""", properties)
    }
}