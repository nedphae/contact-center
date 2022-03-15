package com.qingzhu.imaccess.config

import com.qingzhu.imaccess.controller.CustomerAccessHandler
import com.qingzhu.imaccess.controller.FileUploadDownloadHandler
import com.qingzhu.imaccess.controller.WebSocketAddressController
import com.qingzhu.imaccess.socketio.EchoHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy

@Configuration
@EnableWebFlux
class WebConfiguration : WebFluxConfigurer {
    @Bean
    fun handlerMapping(): HandlerMapping {
        val map = mapOf("/echo" to EchoHandler(), "/test" to EchoHandler())
        val order = 0 // before annotated controllers

        return SimpleUrlHandlerMapping(map, order)
    }

    @Bean
    fun handlerAdapter() =
        WebSocketHandlerAdapter(webSocketService())

    @Bean
    fun webSocketService(): WebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }

    @Bean
    fun routerFunction(
        webSocketAddressController: WebSocketAddressController,
        fileUploadDownloadHandler: FileUploadDownloadHandler,
        customerAccessHandler: CustomerAccessHandler,
    ): RouterFunction<*> {
        return coRouter {
            accept(APPLICATION_JSON).nest {
                // 仅仅测试用的，废弃了
                GET("/websocket-address", webSocketAddressController::getWebSocketAddress)
            }
            // "im".nest { // 即时通讯服务 }
            "/oss".nest {
                listOf("chat", "staff").forEach { path ->
                    val imgBucket = "$path-img"
                    val fileBucket = "$path-file"
                    "/$path".nest {
                        POST("/img") {
                            fileUploadDownloadHandler.upload(it, imgBucket)
                        }
                        GET("/img/{fileName}") {
                            fileUploadDownloadHandler.download(it, imgBucket)
                        }
                        POST("/file") {
                            fileUploadDownloadHandler.upload(it, fileBucket)
                        }
                        GET("/file/{fileName}") {
                            fileUploadDownloadHandler.download(it, fileBucket)
                        }
                    }
                }
            }
            "/access".nest {
                "/customer".nest {
                    POST("/register", customerAccessHandler::register)
                    GET("/has-history-message", customerAccessHandler::hasHistoryMessage)
                    GET("/message/history", customerAccessHandler::loadHistoryMessage)
                    "/comment".nest {
                        POST("", customerAccessHandler::saveComment)
                    }
                }
            }
        }
    }
}