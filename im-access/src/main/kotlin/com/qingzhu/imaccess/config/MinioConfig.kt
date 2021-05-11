package com.qingzhu.imaccess.config

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Minio 配置
 */
@Configuration
class MinioConfig {
    @Bean
    fun minioClient(): MinioClient {
        return MinioClient.builder()
            .endpoint("http://192.168.50.109:9000")
            .credentials("tdKOEz1PoF", "j5zQXblkZK9acA1aHO0JE8QcurKTRGOHiXraYucx")
            .build()
    }
}