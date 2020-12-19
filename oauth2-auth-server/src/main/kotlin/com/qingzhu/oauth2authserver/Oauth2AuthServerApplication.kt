package com.qingzhu.oauth2authserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
class Oauth2AuthServerApplication

fun main(args: Array<String>) {
    runApplication<Oauth2AuthServerApplication>(*args)
}
