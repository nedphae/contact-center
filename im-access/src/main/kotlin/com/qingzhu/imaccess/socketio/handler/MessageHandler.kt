package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.imaccess.domain.query.WebSocketRequestMessage
import com.qingzhu.imaccess.domain.query.messageSubscribe
import com.qingzhu.imaccess.domain.view.MessageResponse
import com.qingzhu.imaccess.service.MessageFilterService
import com.qingzhu.imaccess.service.MessageService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.constant.SocketIONamespace
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
    fun onSend(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequestMessage) {
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
                messageService.send(MessageDto(socketIOClient.sessionId.toString(), it).toMono()).subscribe()
                MessageResponse.fromMessage(it)
            }
        // socketio 支持多次监听同一事件
    }
}