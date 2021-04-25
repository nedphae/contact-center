package com.qingzhu.imaccess.service.disruptor

import com.lmax.disruptor.WorkHandler
import com.qingzhu.imaccess.domain.constant.CreatorType
import com.qingzhu.imaccess.domain.dto.ConversationStatusDto
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.sendWithCallback
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class MessageDisruptorHandler : WorkHandler<Message> {
    override fun onEvent(event: Message) {
        event.toMono()
                .filter { it.organizationId != null }
                .subscribe {
                    val clientFlux = MapUtils.get(Key(it.organizationId!!, it.type, it.to!!))
                    // 推送的消息不需要设置接收者
                    it.organizationId = null
                    it.to = null
                    // 需要记录日志 或增加成功回调 可添加 callback 函数
                    // TODO: 增加回调通知消息送达
                    clientFlux.subscribe { client ->
                        client?.sendWithCallback<Void>(SocketEvent.Message.sync,
                            WebSocketRequest.createRequest(client.sessionId.toString(), it))
                    }
                }
    }
}

@Component
class ConvDisruptorHandler : WorkHandler<ConversationStatusDto> {
    override fun onEvent(event: ConversationStatusDto) {
        event.toMono()
                .subscribe {
                    val clientFlux = MapUtils.get(Key(it.organizationId, CreatorType.STAFF, it.staffId))
                    // 需要记录日志 或增加成功回调 可添加 callback 函数
                    // TODO: 增加回调通知消息送达
                    clientFlux.subscribe { client ->
                        client?.sendWithCallback<Void>(SocketEvent.Message.sync,
                                WebSocketRequest.createRequest(client.sessionId.toString(), it))
                    }
                }
    }
}