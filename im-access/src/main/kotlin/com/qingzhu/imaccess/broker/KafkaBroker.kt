package com.qingzhu.imaccess.broker

import com.lmax.disruptor.dsl.Disruptor
import com.qingzhu.common.util.ApplicationContextManager
import com.qingzhu.imaccess.config.DisruptorEvent
import com.qingzhu.imaccess.config.EventType
import org.apache.kafka.streams.kstream.KStream
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.util.function.Consumer
import java.util.function.Supplier


/**
 * kafka Broker
 * 测试 kafka 消息生产和流式消费
 */
@Configuration
class KafkaBroker(
    val disruptorForContext: Disruptor<DisruptorEvent>
) {
    companion object {
        // 由随机数改为服务器名称（K8S Pod名称）
        val accessServer: String by lazy {
            ApplicationContextManager.applicationContext.environment
                .getProperty("spring.cloud.client.hostname") ?: "im"
        }
    }

    private val logger = LoggerFactory.getLogger(KafkaBroker::class.java)

    @Bean
    fun processor(): Sinks.Many<String> {
        return Sinks.many().multicast().onBackpressureBuffer()
    }

    /**
     * also PollableBean 定时bean
     * see [org.springframework.cloud.function.context.PollableBean]
     */
    @Bean
    fun message(processor: Sinks.Many<String>): Supplier<Flux<String>> {
        return Supplier { processor.asFlux() }
    }

    private fun processConsumer(eventType: (String) -> EventType<*>): Consumer<KStream<Any?, String>> {
        return Consumer { input ->
            input
                .mapValues { value ->
                    Mono.just(eventType(value))
                        .doOnNext {
                            val next = disruptorForContext.ringBuffer.next()
                            val nextEvent = disruptorForContext.ringBuffer.get(next)

                            nextEvent.type = it

                            disruptorForContext.ringBuffer.publish(next)
                        }
                        .onErrorContinue { ex, _ ->
                            logger.error("内部异常：{}", ex)
                        }
                }
                .foreach { _, value ->
                    value.subscribe {
                        if (logger.isDebugEnabled) {
                            logger.debug("消息：{}", it)
                        }
                    }
                }
        }
    }

    /**
     * kafka KStream 流式处理
     * 批量高并发处理，函数式
     */
    @Bean
    fun messageStreamProcess(): Consumer<KStream<Any?, String>> {
        return processConsumer { EventType.Msg(it) }
    }

    /**
     * 推送会话信息
     */
    @Bean
    fun conversationStreamProcess(): Consumer<KStream<Any?, String>> {
        return processConsumer { EventType.Conv(it) }
    }
}