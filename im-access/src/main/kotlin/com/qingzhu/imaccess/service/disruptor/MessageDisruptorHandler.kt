package com.qingzhu.imaccess.service.disruptor

import com.lmax.disruptor.WorkHandler
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.imaccess.domain.dto.ConversationStatusDto
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.domain.view.UpdateMessage
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.sendWithCallback
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class MessageDisruptorHandler : WorkHandler<UpdateMessage> {
    override fun onEvent(event: UpdateMessage) {
        event.toMono()
            .filter { it.message.organizationId != null }
            .subscribe {
                val clientFlux = MapUtils.get(Key(it.message.organizationId!!, it.message.type, it.message.to!!))
                val sentFlux = MapUtils.get(Key(it.message.organizationId!!, it.message.creatorType, it.message.from!!))
                // 需要记录日志 或增加成功回调 可添加 callback 函数
                // TODO: 增加回调通知消息送达
                val messageFromClientId = it.sentClientId
                clientFlux.concatWith(sentFlux).filter { client -> client.sessionId.toString() != messageFromClientId }
                    .doOnNext { _ ->
                        // 推送的消息不需要设置接收者
                        it.message.organizationId = null
                        it.message.to = null
                        it.sentClientId = null
                    }
                    .subscribe { client ->
                        client?.sendWithCallback<Void>(
                            SocketEvent.Message.sync,
                            WebSocketRequest.createRequest(client.sessionId.toString(), it)
                        )
                    }
            }
    }
}

/**
 * TODO: 增加会话的消息丢失检测（更新处理）
 */
@Component
class ConvDisruptorHandler : WorkHandler<ConversationStatusDto> {
    override fun onEvent(event: ConversationStatusDto) {
        event.toMono()
            .subscribe {
                val clientFlux = MapUtils.get(Key(it.organizationId, CreatorType.STAFF, it.staffId))
                // 需要记录日志 或增加成功回调 可添加 callback 函数
                clientFlux.subscribe { client ->
                    client?.sendWithCallback<Void>(
                        SocketEvent.Message.assign,
                        WebSocketRequest.createRequest(client.sessionId.toString(), it)
                    )
                }
            }
    }
}