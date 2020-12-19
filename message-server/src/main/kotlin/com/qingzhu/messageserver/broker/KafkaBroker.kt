package com.qingzhu.messageserver.broker

import org.apache.kafka.streams.kstream.KStream
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import java.util.function.Consumer
import java.util.function.Supplier


/**
 * kafka Broker
 * 测试 kafka 消息生产和流式消费
 */
@Configuration
class KafkaBroker {

    @Bean
    fun processor(): EmitterProcessor<String> {
        return EmitterProcessor.create()
    }

    /**
     * also PollableBean 定时bean
     * see [org.springframework.cloud.function.context.PollableBean]
     */
    @Bean
    fun message(processor: EmitterProcessor<String>): Supplier<Flux<String>> {
        return Supplier { processor }
    }

    /**
     * kafka KStream 流式处理
     * 批量高并发处理，函数式
     */
    @Bean
    fun kStreamProcess(): Consumer<KStream<Any?, String>> {
        return Consumer { input: KStream<Any?, String> -> input.foreach { key: Any?, value: String -> println("Key: $key Value: $value") } }
    }
}