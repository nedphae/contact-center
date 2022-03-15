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
        // 初次重试间隔
        connectionRetryConfig.initialBackoffMillis = 1000 * 10
        connectionRetryConfig.maxBackoffMillis = 1000 * 60 * 5
        // 增长系数 每次增加 0.75 倍时间
        connectionRetryConfig.multiplier = 1.75
        connectionRetryConfig.clusterConnectTimeoutMillis = Long.MAX_VALUE
        // 随机退避的程度，用于减少一定的等待超时
        connectionRetryConfig.jitter = 0.25
        return HazelcastClient.newHazelcastClient(clientConfig)
    }
}