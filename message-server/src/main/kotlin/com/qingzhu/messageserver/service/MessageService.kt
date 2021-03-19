package com.qingzhu.messageserver.service

import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.CreatorType
import com.qingzhu.messageserver.domain.dto.MessageDto
import com.qingzhu.messageserver.domain.value.CustomerToStaff
import com.qingzhu.messageserver.domain.value.Message
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Service
class MessageService(
    private val customerStatusService: CustomerStatusService,
    private val staffStatusService: StaffStatusService,
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val streamBridge: StreamBridge
) {
    private val zSet: ReactiveZSetOperations<String, String> = redisTemplate.opsForZSet()

    /**
     * 客服/客户离线一方离线状态仍然可以发送消息
     * 每次客服登录或客户打开咨询界面时，拉起同步库最新消息
     */
    private fun Mono<CustomerToStaff>.syncMessage(messageDto: MessageDto): Mono<Message> {
        val message = messageDto.message
        val data = message.toJson()
        val from = Mono.just("${message.organizationId}:${message.creatorType.name.toLowerCase()}:${message.from}")
        val to = Mono.just("${message.organizationId}:${message.type.name.toLowerCase()}:${message.to}")
        /**
         * 先写到 redis 再进行推送，相比较于先推送再写redis，延迟虽然增加了
         * 但是用户每次登陆后检查redis大概率不会丢失数据（可能会有重复）
         */
        return from
            .concatWith(to)
            .flatMap {
                // 写扩散
                zSet.add(it, data, message.seqId.toDouble())
            }
            .then(this)
            .flatMapMany {
                customerStatusService.findByUserId(message.organizationId, it.customerId)
                    .flatMapIterable { cs -> cs.clientAccessServerMap.entries }
                    .concatWith(staffStatusService.findStaff(message.organizationId, it.staffId)
                        .flatMapIterable { cs -> cs.clientAccessServerMap.entries })
            }
            // 推送到登陆的其他客户端
            .filter { entry -> entry.key != messageDto.client }
            .map { entry -> entry.value }
            .distinct()
            .doOnNext {
                // 发送消息到kafka
                streamBridge.send("${it}.message", data)
            }
            .collectList()
            .map { message }
    }

    /**
     * 消息发送步骤：
     * 1、保存到 redis zSet (写扩散/发送方，接受方都会写入)
     * 2、查找消息接受方 redis 订阅地址，向 redis 列队写入消息 (推送模式)
     */
    fun send(messageDto: Mono<MessageDto>): Mono<Message> {
        return messageDto
            .publishOn(Schedulers.boundedElastic())
            .flatMap {
                when (it.message.type) {
                    // 封装客服客户 id 对，然后过滤发送消息的客户端ID
                    CreatorType.CUSTOMER -> CustomerToStaff(it.message.to, it.message.from).toMono().syncMessage(it)
                    CreatorType.STAFF -> CustomerToStaff(it.message.from, it.message.to).toMono().syncMessage(it)
                    else -> it.message.toMono()
                }
            }
    }

    fun sendAssignmentEvent() {

    }
}