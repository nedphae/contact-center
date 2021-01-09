package com.qingzhu.imaccess

import com.qingzhu.common.util.ApplicationContextManager
import com.qingzhu.imaccess.config.WebSocketConfigProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(WebSocketConfigProperties::class)
class ImAccessApplication

fun main(args: Array<String>) {
    ApplicationContextManager.applicationContext = runApplication<ImAccessApplication>(*args)
}
