package com.qingzhu.imaccess.util

import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.imaccess.domain.constant.CreatorType
import java.util.concurrent.ConcurrentHashMap

object MapUtils {
    /** 保存 Client 的map到底是现在这种还是 用 clientId 保存？ */
    val clientMap = ConcurrentHashMap<Int, ConcurrentHashMap<CreatorType, ConcurrentHashMap<Long, SocketIOClient>>>()

    /**
     * 类似 java [LocaleObjectCache] 线程安全也只能这样操作了
     */
    fun put(organizationId: Int, role: CreatorType, id: Long, value: SocketIOClient) {
        var orgMap = clientMap[organizationId]
        if (orgMap == null) {
            orgMap = ConcurrentHashMap()
            /**
             * 线程安全双检
             */
            val map = clientMap.putIfAbsent(organizationId, orgMap)
            if (map != null) {
                orgMap = map
            }
        }
        var roleMap = orgMap[role]
        if (roleMap == null) {
            roleMap = ConcurrentHashMap()
            val map = orgMap.putIfAbsent(role, roleMap)
            if (map != null) {
                roleMap = map
            }
        }
        roleMap[id] = value
    }

    fun get(organizationId: Int, role: CreatorType, id: Long): SocketIOClient? {
        val orgMap = clientMap[organizationId]
        val roleMap = orgMap?.get(role)
        return roleMap?.get(id)
    }
}