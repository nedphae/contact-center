package com.qingzhu.imaccess.socketio.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.annotation.OnConnect
import com.corundumstudio.socketio.annotation.OnDisconnect
import com.corundumstudio.socketio.annotation.OnEvent
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.util.toJson
import com.qingzhu.imaccess.domain.constant.OnlineStatus
import com.qingzhu.imaccess.domain.dto.CustomerBaseClientDto
import com.qingzhu.imaccess.domain.query.AssignmentInfo
import com.qingzhu.imaccess.domain.query.WebSocketRequestCustomerConfig
import com.qingzhu.imaccess.domain.query.subscribeWithData
import com.qingzhu.imaccess.domain.view.ConversationView
import com.qingzhu.imaccess.service.DispatchingCenter
import com.qingzhu.imaccess.service.MessageService
import com.qingzhu.imaccess.service.RegisterService
import com.qingzhu.imaccess.service.StaffAdminService
import com.qingzhu.imaccess.socketio.AbstractHandler
import com.qingzhu.imaccess.socketio.constant.SocketEvent
import com.qingzhu.imaccess.socketio.constant.SocketIONamespace
import com.qingzhu.imaccess.socketio.registerName
import com.qingzhu.imaccess.util.Key
import com.qingzhu.imaccess.util.MapUtils
import com.qingzhu.imaccess.util.TimeKey
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.net.InetSocketAddress

/**
 * 客户注册
 * 机器人消息通过 http 接口发送，socket 接口只接入人工消息
 */
@Service
class CustomerEventHandler(
    private val registerService: RegisterService,
    private val dispatchingCenter: DispatchingCenter,
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
) : AbstractHandler(SocketIONamespace.CUSTOMER) {

    @OnConnect
    fun onConnect(client: SocketIOClient) {
    }

    /**
     * 注册客户信息
     */
    @OnEvent(SocketEvent.register)
    fun onRegister(socketIOClient: SocketIOClient, ackRequest: AckRequest, request: WebSocketRequestCustomerConfig) {
        val assignmentInfo = request.toMonoMonad(socketIOClient)
        assignmentInfo
            .doOnNext {
                socketIOClient[registerName] = it.userId
                socketIOClient["organizationId"] = it.organizationId
                MapUtils.Time.markTimeByKey(TimeKey(CreatorType.CUSTOMER, it.userId).apply {
                    organizationId = it.organizationId
                })
            }
            .flatMap { ai ->
                ai.customerConfig.ip = (socketIOClient.remoteAddress as InetSocketAddress).address.hostAddress
                // 从新注册用户信息，以防止用户长时间超时转接人工
                staffAdminService.getShuntByCode(ai.customerConfig.shuntId)
                    .flatMap { sd ->
                        registerService.registerCustomer(ai.customerConfig, sd)
                    }
                    .map { ai }
            }
            .transformDeferredContextual { t, u ->
                t.flatMap {
                    it.customerConfig.ip = (socketIOClient.remoteAddress as InetSocketAddress).address.hostAddress
                    // TODO: 合并注册和更新客户端请求
                    staffAdminService.getShuntByCode(it.customerConfig.shuntId)
                        .flatMap { sd ->
                            registerService.registerCustomer(it.customerConfig, sd)
                        }
                        .flatMap { _ ->
                            // 更新客户信息，保存客户连接到的服务器
                            messageService.updateCustomerClient(
                                CustomerBaseClientDto(
                                    it.organizationId,
                                    it.userId,
                                    u.get<String>("clientId")
                                ).toMono()
                            )
                                // fuck, 总是忘了订阅事件
                                .map { _ -> it }
                        }
                }
            }
            .filter {
                // 设置 客户端 map
                MapUtils.put(Key(it.organizationId, CreatorType.CUSTOMER, it.userId), socketIOClient)
            }
            .flatMap { info ->
                // 检查是否分配了客服
                Mono.justOrEmpty(info.staffId)
                    .map {
                        ConversationView(
                            id = info.conversationId,
                            organizationId = info.organizationId,
                            staffId = info.staffId,
                            userId = info.userId,
                            // 如果分配了，客户端已经有信息了
                            null, null, null, null
                        )
                    }
            }
            // 没有分配过就新分配一个客服
            .switchIfEmpty(assignmentInfo.flatMap { dispatchingCenter.assignmentStaff(it.organizationId, it.userId) })
            // sessionId 为客户端创建，所以掉线重连后用户还是原来的 sessionId，重新注册客户端后会重写状态服务器的记录
            .contextWrite { it.put("clientId", socketIOClient.sessionId.toString()) }
            .doOnDiscard(AssignmentInfo::class.java) {
                // TODO 提醒已经打开其他客户端
                // 如果已经注册过就直接关闭该客户端
                socketIOClient.disconnect()
            }
            .subscribeWithData(ackRequest, request)
    }

    @OnDisconnect
    fun onDisconnect(socketIOClient: SocketIOClient) {
        val (organizationId, userId) = getOrganizationIdAndRegisterName(socketIOClient)
        registerService.unRegisterCustomer(organizationId, userId)
            .flatMap { csd ->
                messageService.findConversationByUserId(organizationId, userId)
                    .filter { csd.onlineStatus == OnlineStatus.OFFLINE && it.interaction == 1 }
                    /*.flatMap {
                        // 发送客户离线消息给客服
                        messageService.send(
                            MessageDto(
                                Message(
                                    organizationId = csd.organizationId,
                                    conversationId = it.id,
                                    to = it.staffId,
                                    type = CreatorType.STAFF,
                                    creatorType = CreatorType.SYS,
                                    content = Content(
                                        MessageType.SYS,
                                        sysCode = SysCode.ONLINE_STATUS_CHANGED,
                                        // 把客户状态发送给客服
                                        textContent = Content.TextContent(csd.toJson())
                                    )
                                )
                            ).toMono()
                        ).map { _ -> it }
                    }*/
                    .flatMap {
                        // 发送会话结束消息给客服
                        messageService.send(
                            MessageDto(
                                Message(
                                    organizationId = csd.organizationId,
                                    conversationId = it.id,
                                    to = it.staffId,
                                    type = CreatorType.STAFF,
                                    creatorType = CreatorType.SYS,
                                    content = Content(
                                        MessageType.SYS,
                                        sysCode = SysCode.CONV_END,
                                        // 把客户状态发送给客服
                                        textContent = Content.TextContent(it.toJson())
                                    )
                                )
                            ).toMono()
                        )
                    }
            }
            .subscribe()

        MapUtils.remove(Key(organizationId, CreatorType.CUSTOMER, userId), socketIOClient)
    }

}