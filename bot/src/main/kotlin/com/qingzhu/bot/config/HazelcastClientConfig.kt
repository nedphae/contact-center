package com.qingzhu.bot.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.core.HazelcastInstance
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastClientConfig {

    @Bean
    fun newHazelcastClient(): HazelcastInstance {
        val clientConfig = ClientConfig()
        clientConfig.clusterName = "message-server"
        clientConfig.networkConfig.addAddress("localhost")// , "10.90.0.2:5702" 如果是 kubernetes 就是 message-server 服务名
        clientConfig.networkConfig.connectionTimeout = 1000 * 60 * 5 // 5分钟内连接消息服务器
        return HazelcastClient.newHazelcastClient(clientConfig)
    }
}