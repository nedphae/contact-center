package com.qingzhu.imaccess.broker

import com.lmax.disruptor.dsl.Disruptor
import com.qingzhu.common.util.ApplicationContextManager
import com.qingzhu.imaccess.config.DisruptorEvent
import com.qingzhu.imaccess.config.EventType
import com.qingzhu.imaccess.util.convertIP2Long
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.ReactiveSubscription
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.listener.Topic
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import reactor.core.publisher.Flux
import java.net.InetAddress

/**
 * 实时消息列队
 * 读取消息服务器下发的通知
 */
@Configuration
class RedisBroker(
        val redisConnectionFactory: ReactiveRedisConnectionFactory,
        val disruptorForContext: Disruptor<DisruptorEvent>
) {

    companion object {
        // 由随机数改为IP地址
        val hashKey: Long by lazy {
            val ip = ApplicationContextManager.applicationContext.environment
                    .getProperty("spring.cloud.client.ip-address") ?: InetAddress.getLocalHost().hostAddress
            convertIP2Long(ip)
        }
    }

    private val logger = LoggerFactory.getLogger(RedisBroker::class.java)

    /**
     * 根据主题 自定义 Redis Container
     */
    @Bean
    fun redisContainer(): Flux<ReactiveSubscription.Message<String, String>> {
        val topics: List<Topic> = listOf(
                // 聊天消息订阅
                ChannelTopic("im:message:${hashKey}")
        )
        val redisContainer = ReactiveRedisMessageListenerContainer(redisConnectionFactory)
        return redisContainer.receive(topics, RedisSerializationContext.SerializationPair
                .fromSerializer(RedisSerializer.string()), RedisSerializationContext.SerializationPair
                .fromSerializer(RedisSerializer.string()))
    }

    fun checkMessageType(channelTopic: String, message: String): EventType<*> {
        return when (channelTopic.substringBeforeLast(":")) {
            "im:message" -> EventType.Msg(message)
            else -> EventType.None(channelTopic)
        }
    }

    /**
     * redis MessageListener 处理消息例子
     */
    @Autowired
    fun defaultAction(redisContainer: Flux<ReactiveSubscription.Message<String, String>>) {
        redisContainer
                .map {
                    checkMessageType(it.channel, it.message)
                }
                .filter { it !is EventType.None }
                .doOnNext {
                    val next = disruptorForContext.ringBuffer.next()
                    val nextEvent = disruptorForContext.ringBuffer.get(next)

                    nextEvent.type = it

                    disruptorForContext.ringBuffer.publish(next)
                }
                .onErrorContinue { ex, _ ->
                    logger.error("内部异常：{}", ex)
                }
                // .retry()
                .subscribe {
                    if (logger.isDebugEnabled) {
                        logger.debug("消息：{}", it)
                    }
                }
    }

}