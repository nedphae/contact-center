package com.qingzhu.messageserver.config

import com.qingzhu.messageserver.controller.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy

@Configuration
@EnableWebFlux
class WebConfiguration : WebFluxConfigurer {

    @Bean
    fun handlerAdapter() =
        WebSocketHandlerAdapter(webSocketService())

    @Bean
    fun webSocketService(): WebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }

    @Bean
    fun routerFunction(
        messageHandler: MessageHandler,
        staffStatusHandler: StaffStatusHandler,
        customerStatusHandler: CustomerStatusHandler,
        conversationStatusHandler: ConversationStatusHandler,
        registerHandler: RegisterHandler
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/message".nest {
                    // 发送消息
                    POST("/send", messageHandler::send)
                    POST("/send/assignment", messageHandler::sendAssignmentEvent)
                }
                "/status".nest {
                    "/register".nest {
                        // 注册客户在线信息
                        POST("/customer", registerHandler::registerCustomer)
                        // 注册客服在线信息
                        POST("/staff", registerHandler::registerStaff)
                    }
                    "/unregister".nest {
                        // 注销客户在线信息
                        PUT("/customer", registerHandler::unregisterCustomer)
                        // 注销客服在线信息
                        PUT("/staff", registerHandler::unregisterStaff)
                    }
                    "/staff".nest {
                        // 获取空闲人工客服
                        GET("/idle", staffStatusHandler::findIdleStaffWithStaffDispatcherDto)
                        // 获取机器人客服
                        GET("/bot/idle", staffStatusHandler::findBotStaffWithStaffDispatcherDto)
                        // 分配客户
                        PUT("/assignment", staffStatusHandler::staffAssignment)
                    }
                    "/customer".nest {
                        // 查询客户指定的接待组id或者客服id
                        GET("/shunt-id", customerStatusHandler::findStaffIdOrShuntId)
                        // 根据 uid 查找客户
                        GET("/find-by-uid", customerStatusHandler::findByUid)
                        // 更新 客户端 ID
                        PUT("/update-client", customerStatusHandler::updateByClientId)
                    }
                    "/conversation".nest {
                        // 创建新会话 分配机器人客服
                        POST("/save", conversationStatusHandler::new)
                        // 根据客户 userId 查找会话
                        GET("/find-by-user-id", conversationStatusHandler::findByUserId)
                        // 结束会话
                        PUT("/end", conversationStatusHandler::end)
                    }
                }
            }
        }
    }
}