package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.transport.NamespaceClient
import com.qingzhu.imaccess.domain.constant.CreatorType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MapUtilsTest {
    @Test
    fun testMap() {
        MapUtils.put(1, CreatorType.STAFF, 5454545, NamespaceClient(null, null))
        val v = MapUtils.get(1, CreatorType.STAFF, 5454545)
        println(v)
        println(MapUtils.clientMap)
    }
}