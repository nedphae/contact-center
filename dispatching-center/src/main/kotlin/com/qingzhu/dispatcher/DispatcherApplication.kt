package com.qingzhu.dispatcher

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class DispatcherApplication

fun main(args: Array<String>) {
    runApplication<DispatcherApplication>(*args)
}
