package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.imaccess.domain.constant.SocketIONamespace
import com.qingzhu.imaccess.domain.query.*
import com.qingzhu.imaccess.domain.view.ConversationView
import com.qingzhu.imaccess.service.DispatchingCenter
import com.qingzhu.imaccess.service.RegisterService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.registerName
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 客户注册
 */
@Service
class CustomerEventHandler(
        private val registerService: RegisterService,
        private val dispatchingCenter: DispatchingCenter
) : AbstractHandler(SocketIONamespace.CUSTOMER) {

    @OnConnect
    fun onConnect(client: SocketIOClient) {
    }

    /**
     * 注册客户信息
     */
    @OnEvent(SocketEvent.register)
    fun onRegister(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequest<CustomerConfig>) {
        val customerConfig = request.toMonoMonad(socketIOClient)
        customerConfig
                .flatMap {
                    //添加 10 分钟内自动转接人工
                    Mono.justOrEmpty(dispatchingCenter.checkIsStaffService(it.organizationId, it.uid))
                }
                .doOnSuccess {
                    socketIOClient[registerName] = it!!.userId
                    socketIOClient["organizationId"] = it.organizationId
                }
                // 如果缓存中还有状态，就不用再次注册了
                .switchIfEmpty(
                        customerConfig
                                .flatMap {
                                    // 向消息服务存储用户消息
                                    registerService.registerCustomer(it)
                                }
                                .doOnSuccess {
                                    socketIOClient[registerName] = it!!.userId
                                    socketIOClient["organizationId"] = it.organizationId
                                }
                                .flatMap {
                                    Mono.empty<ConversationView>()
                                }
                )
                .subscribeWithData(ackRequest, request)
    }

    @OnDisconnect
    fun onDisconnect(socketIOClient: SocketIOClient) {
        val (organizationId, userId) = getOrganizationIdAndRegisterName(socketIOClient)
        registerService.unRegisterCustomer(organizationId, userId)
    }

    /**
     * 转接到人工座席
     * Long: userId
     */
    @OnEvent(SocketEvent.turnToStaff)
    fun onTurnToStaff(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequest<Long>) {
        // 获取客户的接待组ID
        val (organizationId, userId) = getOrganizationIdAndRegisterName(socketIOClient)
        Mono.justOrEmpty(dispatchingCenter.assignmentStaff(organizationId, userId))
                .subscribeWithData(ackRequest, request)
    }
}