package com.qingzhu.imaccess.service.disruptor

import com.lmax.disruptor.WorkHandler
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.sendWithCallback
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono

@Component
class MessageDisruptorHandler : WorkHandler<Message> {
    override fun onEvent(event: Message) {
        event.toMono()
                .filter { it.organizationId != null }
                .subscribe {
                    val client = MapUtils.get(it.organizationId!!, it.type, it.to!!)
                    // 推送的消息不需要设置接收者
                    it.organizationId = null
                    it.to = null
                    // 需要记录日志 或增加成功回调 可添加 callback 函数
                    client?.sendWithCallback(SocketEvent.Message.sync,
                            WebSocketRequest.createRequest(client.sessionId.toString(), it))
                }
    }
}