package com.qingzhu.messageserver.service

import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.CreatorType
import com.qingzhu.messageserver.domain.dto.Message
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class MessageService(
        private val customerStatusService: CustomerStatusService,
        private val staffStatusService: StaffStatusService,
        redisTemplate: ReactiveRedisTemplate<String, String>,
        private val streamBridge: StreamBridge
) {
    private val zSet: ReactiveZSetOperations<String, String> = redisTemplate.opsForZSet()

    private fun Mono<String>.syncMessage(message: Message): Mono<Message> {
        val data = message.toJson()
        val from = Mono.just("${message.organizationId}:${message.creatorType.name.toLowerCase()}:${message.from}")
        val to = Mono.just("${message.organizationId}:${message.type.name.toLowerCase()}:${message.to}")
        return this
                .doOnNext {
                    // 发送消息到kafka
                    streamBridge.send("${it}.message", data)
                }
                .then(from)
                .concatWith(to)
                .flatMap {
                    // 写扩散
                    zSet.add(it, data, message.seqId.toDouble())
                }
                .collectList()
                .map { message }
    }

    /**
     * 消息发送步骤：
     * 1、保存到 redis zSet (写扩散/发送方，接受方都会写入)
     * 2、查找消息接受方 redis 订阅地址，向 redis 列队写入消息 (推送模式)
     */
    fun send(message: Mono<Message>): Mono<Message> {
        return message
                .publishOn(Schedulers.boundedElastic())
                .flatMap {
                    when (it.type) {
                        CreatorType.CUSTOMER -> customerStatusService.findByUserId(it.organizationId, it.to)
                                .flatMap { cs -> Mono.justOrEmpty(cs.hashKey) }
                                .syncMessage(it)
                        CreatorType.STAFF -> staffStatusService.findStaff(it.organizationId, it.to)
                                .flatMap { cs -> Mono.justOrEmpty(cs.hashKey) }
                                .syncMessage(it)
                        else -> message
                    }
                }
    }

    fun sendAssignmentEvent() {

    }
}