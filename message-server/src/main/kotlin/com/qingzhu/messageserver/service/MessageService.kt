package com.qingzhu.messageserver.service

import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.constant.CreatorType
import com.qingzhu.messageserver.domain.dto.MessageDto
import com.qingzhu.messageserver.domain.dto.UpdateMessage
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.domain.value.CustomerToStaff
import com.qingzhu.messageserver.domain.value.Message
import kotlinx.coroutines.reactive.awaitSingle
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
    private fun Mono<CustomerToStaff>.syncMessage(messageDto: MessageDto): Mono<Boolean> {
        val message = messageDto.message

        val from = Mono.just("${message.organizationId}:${message.creatorType.name.toLowerCase()}:${message.from}")
        val to = Mono.just("${message.organizationId}:${message.type.name.toLowerCase()}:${message.to}")

        lateinit var staffUpdateMessage: UpdateMessage
        lateinit var customerUpdateMessage: UpdateMessage
        val staff = this.flatMap { staffStatusService.findStaff(message.organizationId, it.staffId) }
                .map {
                    staffUpdateMessage = UpdateMessage(it.pts ?: message.seqId, message, messageDto.client)
                    if (message.type == CreatorType.STAFF) {
                        it.pts = message.seqId
                        staffStatusService.saveStatus(it)
                    }
                    it
                }
        val customer = this.flatMap { customerStatusService.findByUserId(message.organizationId, it.customerId) }
                .map {
                    customerUpdateMessage = UpdateMessage(it.pts ?: message.seqId, message, messageDto.client)
                    if (message.type == CreatorType.CUSTOMER) {
                        it.pts = message.seqId
                        customerStatusService.saveStatus(it)
                    }
                    it
                }

        val data = message.toJson()
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
                customer.flatMapIterable { cs -> cs.clientAccessServerMap.entries }
                    .concatWith(staff.flatMapIterable { staffStatus -> staffStatus.clientAccessServerMap.entries })
            }
            // 推送到登陆的其他客户端
            .filter { entry -> entry.key != messageDto.client }
            .map { entry -> entry.value }
            .distinct()
            .doOnNext {
                // 发送消息到kafka
                when (message.type) {
                    CreatorType.CUSTOMER -> streamBridge.send("${it}.message", customerUpdateMessage.toJson())
                    CreatorType.STAFF -> streamBridge.send("${it}.message", staffUpdateMessage.toJson())
                    else -> { }
                }
            }
            .collectList()
            .map { true }
    }

    /**
     * 消息发送步骤：
     * 1、保存到 redis zSet (写扩散/发送方，接受方都会写入)
     * 2、查找消息接受方 redis 订阅地址，向 redis 列队写入消息 (推送模式)
     */
    fun send(messageDto: Mono<MessageDto>): Mono<Boolean> {
        return messageDto
            .publishOn(Schedulers.boundedElastic())
            .flatMap {
                when (it.message.type) {
                    // 封装客服客户 id 对，然后过滤发送消息的客户端ID
                    CreatorType.CUSTOMER -> CustomerToStaff(it.message.to, it.message.from).toMono().syncMessage(it)
                    CreatorType.STAFF -> CustomerToStaff(it.message.from, it.message.to).toMono().syncMessage(it)
                    else -> Mono.just(false)
                }
            }
    }

    /**
     * bot 消息处理，写入到用户的 redis zSet 列表
     */
    suspend fun syncBotMessage(message: Message): Boolean {
        val isBot = message.creatorType == CreatorType.STAFF
        val userId = if (isBot) message.to else message.from
        val key = "${message.organizationId}:${CreatorType.CUSTOMER.name.toLowerCase()}:${userId}"
        return zSet.add(key, message.toJson(), message.seqId.toDouble()).awaitSingle()
    }

    suspend fun transformTo() {
        TODO("转接客户，把客户聊天信息批量写入到客服的 zSet")
    }

    /**
     * websocket发送 Assignment 事件给客服
     */
    fun sendAssignmentEvent(conversationStatusDto: Mono<ConversationStatus>): Mono<Unit> {
        val conversationStatusDtoCache = conversationStatusDto.cache().checkpoint("检查status")
        return conversationStatusDtoCache
                .filter { it.interaction == 1 }
                .flatMapMany {
                    staffStatusService.findStaff(it.organizationId, it.staffId)
                            .flatMapIterable { cs -> cs.clientAccessServerMap.entries }
                }
                .map { entry -> entry.value }
                .distinct()
                .doOnNext {
                    // 发送消息到kafka
                    streamBridge.send("${it}.conv", conversationStatusDtoCache.block())
                }
                .collectList()
                .flatMap { Mono.empty() }
    }
}