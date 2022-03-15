package com.qingzhu.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class BotApplication

fun main(args: Array<String>) {
    runApplication<BotApplication>(*args)
}
