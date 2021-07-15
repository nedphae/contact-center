package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.streams.toList

data class Key(
    val organizationId: Int,
    val role: CreatorType,
    val id: Long
)

object MapUtils {
    /** 保存 Client 的map到底是现在这种还是 用 clientId 保存？ */
    val clientMap =
        ConcurrentHashMap<Int, ConcurrentHashMap<CreatorType, ConcurrentHashMap<Long, MutableSet<SocketIOClient>>>>()

    object Time {
        // TODO 使用 redis lua 脚本替换单机聊天统计
        private val timeMap = ConcurrentHashMap<Int, ConcurrentHashMap<Key, Long>>()

        fun markTimeByKey(key: Key) {
            var orgMap = timeMap[key.organizationId]
            if (orgMap == null) {
                orgMap = ConcurrentHashMap()
                /**
                 * 线程安全双检
                 */
                val map = timeMap.putIfAbsent(key.organizationId, orgMap)
                if (map != null) {
                    orgMap = map
                }
            }
            orgMap[key] = System.currentTimeMillis()
        }

        /**
         * 获取过期的 key
         */
        fun getExpiredKey(organizationId: Int, role: CreatorType, duration: Duration): List<Key> {
            return timeMap[organizationId]?.let { map ->
                map.entries
                    .parallelStream()
                    .filter {
                        it.value <= System.currentTimeMillis() - duration.toMillis() &&
                                it.key.role == role
                    }
                    .map { it.key }
                    .toList()
            } ?: emptyList()
        }

        fun removeKey(key: Key) {
            val orgMap = timeMap[key.organizationId]
            if (orgMap != null) {
                orgMap.remove(key)
                if (orgMap.isEmpty()) {
                    timeMap.remove(key.organizationId, orgMap)
                }
            }

        }
    }

    /**
     * 类似 java [LocaleObjectCache] 线程安全也只能这样操作了
     */
    fun put(key: Key, vararg value: SocketIOClient): Boolean {
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
        var result = list.addAll(value)
        result = roleMap.putIfAbsent(key.id, list)?.addAll(value) ?: result
        return result
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
                Time.removeKey(key)
                roleMap.remove(key.id, it)
            }
        }
    }

}