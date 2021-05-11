package com.qingzhu.messageserver.broker

import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.kstream.KStream
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier


/**
 * kafka Broker
 * 测试 kafka 消息生产和流式消费
 * StreamBridge 动态绑定
 * 不能使用 @EnableBinding
 */
@Configuration
class KafkaBroker {

    @Bean
    fun processor(): Sinks.Many<Message<String>> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }

    /**
     * also PollableBean 定时bean
     * see [org.springframework.cloud.function.context.PollableBean]
     */
    @Bean
    fun message(processor: Sinks.Many<Message<String>>): Supplier<Flux<Message<String>>> {
        return Supplier { processor.asFlux() }
    }

    /**
     * kafka KStream 流式处理
     * 批量高并发处理，函数式
     */
    @Bean
    fun outKStreamProcess(): java.util.function.Function<KStream<Any?, String>, KStream<String?, String>> {
        return java.util.function.Function { input ->
            input
                // .filter { _, value -> value.headers["hashKey"] == "1" }
                .map { key, value ->
                    println("outKStreamProcess: Key: $key Value: $value")
                    KeyValue("im", "outKStreamProcess Forward with :\t $value")
                }
        }
    }

    /**
     * kafka KStream 流式处理
     * 批量高并发处理，函数式
     */
    @Bean
    fun inKStreamProcess(): Consumer<KStream<String?, String>> {
        return Consumer { input ->
            input.foreach { key, value ->
                println("inKStreamProcess: Key: $key Value: $value")
            }
        }
    }
}