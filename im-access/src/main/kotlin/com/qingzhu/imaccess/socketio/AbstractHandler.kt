package com.qingzhu.imaccess.socketio

import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.imaccess.socketio.constant.SocketIONamespace

/**
 * namespace 注册方法
 */
abstract class AbstractHandler(private vararg val namespace: SocketIONamespace) {

    fun getNamespace(): List<String> {
        return namespace.map { it.namespace }
    }

    fun getOrganizationIdAndRegisterName(socketIOClient: SocketIOClient): Pair<Int, Long> {
        val organizationId: Int = socketIOClient["organizationId"]
        val registerId: Long = socketIOClient[registerName]
        return organizationId to registerId
    }
}

const val registerName = "regId"