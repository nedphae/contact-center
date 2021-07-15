package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.imaccess.domain.query.WebSocketRequest
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.sendWithCallback
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.Duration

/**
 * 自动踢人，超时关闭人工会话
 */
@Service
class AutoKickOutService(
    private val registerService: RegisterService,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        Flux.interval(Duration.ZERO, Duration.ofSeconds(10), Schedulers.boundedElastic())
            .flatMapIterable {
                // TODO 通过配置获取
                MapUtils.Time.getExpiredKey(9491, CreatorType.CUSTOMER, Duration.ofMinutes(15))
            }
            .flatMapSequential { MapUtils.get(it).map { socket -> it to socket } }
            .map { (key, socket) ->
                // TODO 消息通过配置获取
                socket.sendWithCallback<Void>(
                    SocketEvent.IO.closed,
                    WebSocketRequest.createRequest(socket.sessionId.toString(), "连接超时断开")
                ) {
                    registerService.unRegisterCustomer(key.organizationId, key.id, CreatorType.SYS)
                    MapUtils.remove(Key(key.organizationId, CreatorType.CUSTOMER, key.id), socket)
                    socket.disconnect()
                }
            }
            .thenMany(MapUtils.Time.getExpiredKey(9491, CreatorType.STAFF, Duration.ofMinutes(2)).toFlux())
            .map {
                TODO("客服离线超时，发送关闭信号给其客户")
            }
            .subscribe()
    }
}