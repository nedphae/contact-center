package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.imaccess.domain.constant.CreatorType
import reactor.core.publisher.Flux
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

data class Key(
    val organizationId: Int,
    val role: CreatorType,
    val id: Long
)

object MapUtils {
    /** 保存 Client 的map到底是现在这种还是 用 clientId 保存？ */
    val clientMap = ConcurrentHashMap<Int, ConcurrentHashMap<CreatorType, ConcurrentHashMap<Long, MutableSet<SocketIOClient>>>>()

    /**
     * 类似 java [LocaleObjectCache] 线程安全也只能这样操作了
     */
    fun put(key: Key, vararg value: SocketIOClient) {
        var orgMap = clientMap[key.organizationId]
        if (orgMap == null) {
            orgMap = ConcurrentHashMap()
            /**
             * 线程安全双检
             */
            val map = clientMap.putIfAbsent(key.organizationId, orgMap)
            if (map != null) {
                orgMap = map
            }
        }
        var roleMap = orgMap[key.role]
        if (roleMap == null) {
            roleMap = ConcurrentHashMap()
            val map = orgMap.putIfAbsent(key.role, roleMap)
            if (map != null) {
                roleMap = map
            }
        }
        val list = roleMap[key.id] ?: CopyOnWriteArraySet()
        list.addAll(value)
        roleMap.putIfAbsent(key.id, list)?.addAll(value)
    }

    fun get(key: Key): Flux<SocketIOClient> {
        val orgMap = clientMap[key.organizationId]
        val roleMap = orgMap?.get(key.role)
        return roleMap?.get(key.id)?.let { Flux.fromIterable(it) } ?: Flux.empty()
    }

    fun remove(key: Key, value: SocketIOClient) {
        val orgMap = clientMap[key.organizationId]
        val roleMap = orgMap?.get(key.role)
        val list = roleMap?.get(key.id)
        list?.also {
            it.remove(value)
            if (it.isEmpty()) {
                roleMap.remove(key.id)
            }
        }
    }

}