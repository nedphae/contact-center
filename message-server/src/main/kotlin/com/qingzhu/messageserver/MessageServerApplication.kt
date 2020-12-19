package com.qingzhu.messageserver

import com.qingzhu.common.util.ApplicationContextManager
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
class MessageServerApplication

fun main(args: Array<String>) {
    ApplicationContextManager.applicationContext = runApplication<MessageServerApplication>(*args)
}

