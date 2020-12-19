package com.qingzhu.imaccess.config

import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.bracket.onError
import com.corundumstudio.socketio.AuthorizationListener
import com.corundumstudio.socketio.SocketIOServer
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner
import com.corundumstudio.socketio.handler.SuccessAuthorizationListener
import com.corundumstudio.socketio.protocol.JacksonJsonSupport
import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.agent.model.NewService
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.qingzhu.imaccess.socketio.AbstractHandler
import kotlinx.coroutines.Dispatchers
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.annotation.PreDestroy
import com.corundumstudio.socketio.Configuration as NettyConfig


@Configuration
class NettyWebSocketAutoConfiguration(
        val properties: ConsulDiscoveryProperties,
        val webSocketConfigProperties: WebSocketConfigProperties,
        val consulClient: ConsulClient
) {
    private val logger = LogFactory.getLog(NettyWebSocketAutoConfiguration::class.java)

    @Autowired
    lateinit var socketIOServer: SocketIOServer

    @Bean
    fun configSocketIOServer(authorizationListener: ObjectProvider<AuthorizationListener>): SocketIOServer {
        val config = NettyConfig()
        config.port = webSocketConfigProperties.port
        config.workerThreads = webSocketConfigProperties.workerThreads
        config.authorizationListener = authorizationListener.getIfAvailable { SuccessAuthorizationListener() }
        config.socketConfig.isReuseAddress = true
        config.socketConfig.soLinger = 0
        config.socketConfig.isTcpNoDelay = true
        config.socketConfig.isTcpKeepAlive = true
        // 添加 kotlin 支持
        config.jsonSupport = JacksonJsonSupport(KotlinModule())

        return SocketIOServer(config)
    }

    @Bean
    fun springAnnotationScanner(socketServer: SocketIOServer?): SpringAnnotationScanner? {
        return SpringAnnotationScanner(socketServer)
    }

    /**
     * 自动注册全部 handler
     */
    @Autowired
    fun configNamespace(socketIOServer: SocketIOServer, eventHandler: ObjectProvider<AbstractHandler>) {
        eventHandler.forEach { handler ->
            handler.getNamespace().forEach { name ->
                socketIOServer.addNamespace(name).addListeners(handler)
            }
        }
    }

    @PreDestroy
    fun destroy() {
        consulClient.agentServiceDeregister(webSocketConfigProperties.myServiceId)
        socketIOServer.stop()
    }

    /**
     * 等待 spring 启动完毕
     * 需要注册 namespace
     */
    @Bean
    fun runBean() = CommandLineRunner {
        socketIOServer.start()
        // 注册到微服务
        val newService = NewService()
        newService.id = webSocketConfigProperties.myServiceId
        newService.name = webSocketConfigProperties.name
        newService.address = properties.ipAddress
        newService.port = webSocketConfigProperties.port
        val check = NewService.Check()
        check.http = properties.healthCheckUrl
        // check.interval = properties.healthCheckInterval
        check.ttl = "10s" // properties.healthCheckInterval
        check.deregisterCriticalServiceAfter = "1000s"
        check.status = "passing"
        newService.check = check
        IO {
            logger.info("Register SocketIO")
            consulClient.agentServiceRegister(newService)
        }
                .onError { IO { logger.error(it) } }
                .fork(Dispatchers.IO)
                .flatMap { IO.fx { it.join().bind() } }
                .runAsync { IO.never }
    }
}