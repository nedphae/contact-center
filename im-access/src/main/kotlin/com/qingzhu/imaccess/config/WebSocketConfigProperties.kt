package com.qingzhu.imaccess.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding


@ConfigurationProperties("spring.netty-websocket")
@ConstructorBinding
data class WebSocketConfigProperties(
    val name: String = "websocket-server",
    val port: Int = 8090,
    val workerThreads: Int = 80,
    val serviceId: String = "websocket-server:8090"
) {
    val myServiceId: String = serviceId
        get() {
            return field.ifEmpty {
                "${name}:${port}"
            }
        }
}