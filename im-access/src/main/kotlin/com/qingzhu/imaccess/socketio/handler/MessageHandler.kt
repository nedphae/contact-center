package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.imaccess.domain.constant.SocketIONamespace
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.domain.query.messageSubscribe
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.domain.view.MessageResponse
import com.qingzhu.imaccess.service.MessageFilterService
import com.qingzhu.imaccess.service.MessageService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono

/**
 * 客服和客户共用的消息 handler
 */
@Service
class MessageHandler(
        private val messageService: MessageService,
        private val messageFilterService: MessageFilterService
) : AbstractHandler(SocketIONamespace.STAFF, SocketIONamespace.CUSTOMER) {
    /**
     * 发送聊天消息
     */
    @OnEvent(SocketEvent.Message.send)
    fun onSend(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequest<Message>) {
        request.toMonoMonad(socketIOClient)
                .doOnNext {
                    val (organizationId, from) = getOrganizationIdAndRegisterName(socketIOClient)
                    it.organizationId = organizationId
                    // 重写 发送方
                    it.from = from
                }
                // 过滤消息
                .transform(messageFilterService::filter)
                .messageSubscribe(ackRequest, request) {
                    messageService.send(it.toMono())
                    MessageResponse.fromMessage(it)
                }
        // TODO 客户特定时间没有说话就踢出咨询 修改放到接入服务器进行
        // socketio 支持多次监听同一事件
    }

    /**
     * 客户端手动同步聊天消息
     */
    @OnEvent(SocketEvent.Message.sync)
    fun onSync() {

    }
}