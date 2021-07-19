package com.qingzhu.bot.config

import com.hazelcast.client.HazelcastClient
import com.hazelcast.client.config.ClientConfig
import com.hazelcast.client.config.ClientConnectionStrategyConfig
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
        val connectionStrategyConfig = clientConfig.connectionStrategyConfig
        // 配置 客户端 连接策略：阻塞直到连接上集群
        connectionStrategyConfig.reconnectMode = ClientConnectionStrategyConfig.ReconnectMode.ON
        val connectionRetryConfig = connectionStrategyConfig.connectionRetryConfig
        connectionRetryConfig.initialBackoffMillis = 1000 * 60 * 2
        connectionRetryConfig.maxBackoffMillis = 1000 * 60 * 10
        connectionRetryConfig.multiplier = 2.0
        connectionRetryConfig.clusterConnectTimeoutMillis = Long.MAX_VALUE
        connectionRetryConfig.jitter = 0.2
        return HazelcastClient.newHazelcastClient(clientConfig)
    }
}