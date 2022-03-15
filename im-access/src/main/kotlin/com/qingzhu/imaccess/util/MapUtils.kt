package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.streams.toList

data class Key(
    var organizationId: Int,
    val role: CreatorType,
    val id: Long
)

data class TimeKey(
    val role: CreatorType,
    val id: Long,
) {
    var organizationId: Int = -1
}

object MapUtils {
    /** 保存 Client 的map到底是现在这种还是 用 clientId 保存？ */
    val clientMap =
        ConcurrentHashMap<Int, ConcurrentHashMap<CreatorType, ConcurrentHashMap<Long, MutableSet<SocketIOClient>>>>()

    /**
     * 获取当前在线的全部客户
     */
    fun getAllCustomerClient(): List<Pair<Int, Long>> {
        return clientMap.entries.map {
            it.value[CreatorType.CUSTOMER]?.keys()?.toList()?.map { id -> it.key to id } ?: emptyList()
        }.flatten()
    }

    object Time {
        // TODO 使用 redis lua 脚本替换单机聊天统计
        private val timeMap = ConcurrentHashMap<TimeKey, Instant>()

        fun markTimeByKey(key: TimeKey) {
            timeMap[key] = Instant.now()
        }

        /**
         * 获取过期的 key
         */
        fun getExpiredKey(role: CreatorType, duration: Duration): List<TimeKey> {
            return timeMap.entries
                .parallelStream()
                .filter {
                    it.value.isBefore(Instant.now().minusSeconds(duration.toSeconds())) &&
                            it.key.role == role
                }
                .map { it.key }
                .toList()
        }

        fun getExpiredKey(role: CreatorType, getDuration: (Int) -> Duration): List<TimeKey> {
            return timeMap.entries
                .parallelStream()
                .filter {
                    it.value.isBefore(Instant.now().minusSeconds(getDuration(it.key.organizationId).toSeconds())) &&
                            it.key.role == role
                }
                .map { it.key }
                .toList()
        }

        fun removeKey(key: TimeKey) {
            timeMap.remove(key)
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
                roleMap.remove(key.id, it)
            }
        }
    }

}