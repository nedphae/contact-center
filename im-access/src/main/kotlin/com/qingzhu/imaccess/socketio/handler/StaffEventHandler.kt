package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.imaccess.domain.constant.OnlineStatus
import com.qingzhu.imaccess.domain.query.WebSocketRequestStaffConfig
import com.qingzhu.imaccess.domain.query.subscribeWithoutData
import com.qingzhu.imaccess.service.DispatchingCenter
import com.qingzhu.imaccess.service.RegisterService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.constant.SocketIONamespace
import com.qingzhu.imaccess.socketio.registerName
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import com.qingzhu.imaccess.util.TimeKey
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

/**
 * 客服注册
 */
@Service
class StaffEventHandler(
    private val registerService: RegisterService,
    private val dispatchingCenter: DispatchingCenter,
) : AbstractHandler(SocketIONamespace.STAFF) {

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
                it.role = socketIOClient.handshakeData.getSingleUrlParam("role")
                socketIOClient[registerName] = it.staffId
                socketIOClient["organizationId"] = it.organizationId
            }
            .flatMap {
                val key = Key(it.organizationId!!, CreatorType.STAFF, it.staffId!!)
                MapUtils.put(key, socketIOClient)
                MapUtils.Time.removeKey(TimeKey(CreatorType.STAFF, it.staffId!!).apply {
                    this.organizationId = it.organizationId!!
                })
                // 向消息服务存储客服消息
                registerService.registerStaff(it)
                    .flatMap { ssd ->
                        if (ssd.onlineStatus == OnlineStatus.ONLINE) {
                            // 上线通知调度排队客户
                            dispatchingCenter.assignmentFromQueueForStaff(ssd)
                                .then(Mono.justOrEmpty(ssd))
                        } else {
                            Mono.justOrEmpty(ssd)
                        }

                    }
            }
            .contextWrite { it.put("clientId", socketIOClient.sessionId.toString()) }
            .subscribeWithoutData(ackRequest, request)
    }

    @OnDisconnect
    fun onDisconnect(socketIOClient: SocketIOClient) {
        val (organizationId, staffId) = getOrganizationIdAndRegisterName(socketIOClient)
        registerService.unRegisterStaff(organizationId, staffId, socketIOClient.sessionId.toString())
        MapUtils.remove(Key(organizationId, CreatorType.STAFF, staffId), socketIOClient)
        MapUtils.Time.markTimeByKey(TimeKey(CreatorType.STAFF, staffId).apply { this.organizationId = organizationId })
        socketIOClient.disconnect()
    }
}