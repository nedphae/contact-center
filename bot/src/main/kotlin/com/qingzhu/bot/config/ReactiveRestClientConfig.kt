package com.qingzhu.bot.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients
import org.springframework.data.elasticsearch.config.AbstractReactiveElasticsearchConfiguration

@Configuration
class ReactiveRestClientConfig : AbstractReactiveElasticsearchConfiguration() {

    override fun reactiveElasticsearchClient(): ReactiveElasticsearchClient {
        val clientConfiguration = ClientConfiguration.builder()
            .connectedTo("192.168.50.104:9200")
            .build();
        return ReactiveRestClients.create(clientConfiguration)
    }
}