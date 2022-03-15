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
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class MessageDisruptorHandler : WorkHandler<UpdateMessage> {
    override fun onEvent(event: UpdateMessage) {
        event.toMono()
            .filter { it.message.organizationId != null }
            .subscribe {
                val organizationId = it.message.organizationId
                val clientFlux = Mono.justOrEmpty(it.message.to)
                    .flatMapMany { to -> MapUtils.get(Key(organizationId!!, it.message.type, to)) }
                    .concatWith(
                        Mono.justOrEmpty(it.message.from)
                            .flatMapMany { from -> MapUtils.get(Key(organizationId!!, it.message.creatorType, from)) })

                // 需要记录日志 或增加成功回调 可添加 callback 函数
                // TODO: 增加回调通知消息送达
                val messageFromClientId = it.sentClientId
                clientFlux.filter { client -> client.sessionId.toString() != messageFromClientId }
                    .doOnNext { _ ->
                        // 推送的消息不需要设置接收者
                        it.message.organizationId = null
                        it.sentClientId = null
                    }
                    .subscribe { client ->
                        client?.sendWithCallback<String>(
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
                    client?.sendWithCallback<String>(
                        SocketEvent.Message.assign,
                        WebSocketRequest.createRequest(client.sessionId.toString(), it)
                    )
                }
            }
    }
}