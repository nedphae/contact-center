package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.imaccess.domain.constant.CreatorType
import com.qingzhu.imaccess.domain.constant.SocketIONamespace
import com.qingzhu.imaccess.domain.query.WebSocketRequestStaffConfig
import com.qingzhu.imaccess.domain.query.subscribeWithoutData
import com.qingzhu.imaccess.service.RegisterService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.messageAck
import com.qingzhu.imaccess.socketio.registerName
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.stereotype.Service

/**
 * 客服注册
 */
@Service
class StaffEventHandler(private val registerService: RegisterService) : AbstractHandler(SocketIONamespace.STAFF) {

    @OnConnect
    fun onConnect(client: SocketIOClient) {
        // 修改为 从 jwt 中读取信息
        // 精简 onConnect HandshakeData
        // 因为 HandshakeData 不能自动封装数据，所以不再使用 握手数据
        // 改为由单独的 socket 消息事件传递数据
    }

    /**
     * 注册客服信息
     */
    @OnEvent("register")
    fun onRegister(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequestStaffConfig) {
        request.toMonoMonad(socketIOClient)
            .doOnNext {
                it.staffId = socketIOClient.handshakeData.getSingleUrlParam("sid").toLong()
                it.organizationId = socketIOClient.handshakeData.getSingleUrlParam("oid").toInt()
                socketIOClient[registerName] = it.staffId
                socketIOClient["organizationId"] = it.organizationId
            }
            .flatMap {
                val key = Key(it.organizationId!!, CreatorType.STAFF, it.staffId!!)
                MapUtils.put(key, socketIOClient)
                // 向消息服务存储客服消息
                registerService.registerStaff(it)
            }
            .contextWrite { it.put("clientId", socketIOClient.sessionId.toString()) }
            .subscribeWithoutData(ackRequest, request)
    }

    @OnDisconnect
    fun onDisconnect(socketIOClient: SocketIOClient) {
        val (organizationId, staffId) = getOrganizationIdAndRegisterName(socketIOClient)
        registerService.unRegisterStaff(organizationId, staffId)
        MapUtils.remove(Key(organizationId, CreatorType.STAFF, staffId), socketIOClient)
    }
}