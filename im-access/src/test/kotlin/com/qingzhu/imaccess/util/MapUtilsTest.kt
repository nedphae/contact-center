package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.transport.NamespaceClient
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MapUtilsTest {
    @Test
    fun testMap() {
        MapUtils.put(Key(1, CreatorType.STAFF, 5454545), NamespaceClient(null, null))
        val v = MapUtils.get(Key(1, CreatorType.STAFF, 5454545))
        println(v)
        println(MapUtils.clientMap)
    }

    @Test
    fun testMod() {
        // test &  // h & (length-1) = h % length
        val h = 91
        val length = 92
        println(h and (length - 1))
        println(h % length)
    }

    @Test
    fun testHash() {
        val hash = TimeKey(CreatorType.STAFF, 1).apply { organizationId = 9491 }.hashCode()
        assertEquals(hash, TimeKey(CreatorType.STAFF, 1).hashCode())
    }
}