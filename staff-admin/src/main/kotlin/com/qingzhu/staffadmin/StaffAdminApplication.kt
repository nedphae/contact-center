package com.qingzhu.staffadmin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class StaffAdminApplication

fun main(args: Array<String>) {
    runApplication<StaffAdminApplication>(*args)
}
