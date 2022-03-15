package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.dto.MessageDto
import com.qingzhu.common.domain.shared.msg.dto.UpdateMessage
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.domain.entity.ChatMessagePO
import com.qingzhu.messageserver.domain.entity.ConversationStatus
import com.qingzhu.messageserver.domain.value.CustomerToStaff
import com.qingzhu.messageserver.mapper.ChatMessageMapper
import com.qingzhu.messageserver.repository.ChatMessagePORepository
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class MessageService(
    private val customerStatusService: CustomerStatusService,
    private val staffStatusService: StaffStatusService,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val streamBridge: StreamBridge,
    private val chatMessagePORepository: ChatMessagePORepository,
    private val conversationStatusService: ConversationStatusService,
) {
    private val zSet: ReactiveZSetOperations<String, String> = redisTemplate.opsForZSet()

    /**
     * 客服/客户离线一方离线状态仍然可以发送消息
     * 每次客服登录或客户打开咨询界面时，拉起同步库最新消息
     */
    private fun Mono<CustomerToStaff>.syncMessage(messageDto: MessageDto): Mono<Boolean> {
        val message = messageDto.message

        val from = Mono.justOrEmpty(message.from)
            .map { "msg:${message.organizationId}:${message.creatorType.name.toLowerCase()}:${it}" }
        val to = Mono.just("msg:${message.organizationId}:${message.type.name.toLowerCase()}:${message.to}")

        lateinit var staffUpdateMessage: UpdateMessage
        lateinit var customerUpdateMessage: UpdateMessage
        val staff = this.flatMap { Mono.justOrEmpty(it.staffId) }
            .flatMap { staffId ->
                staffStatusService.consistencyUpdate(message.organizationId!!, staffId) {
                    map {
                        staffUpdateMessage = UpdateMessage(it.pts ?: message.seqId, message, messageDto.clientId)
                        if (message.type == CreatorType.STAFF) {
                            it.pts = message.seqId
                        }
                        it
                    }
                }
            }
        val customer = this.flatMap { Mono.justOrEmpty(it.customerId) }
            .flatMap { customerStatusService.findByUserId(message.organizationId!!, it) }
            .doOnNext {
                customerUpdateMessage = UpdateMessage(it.pts ?: message.seqId, message, messageDto.clientId)
            }
            .cache()

        val data = message.toJson()
        /**
         * 先写到 redis 再进行推送，相比较于先推送再写redis，延迟虽然增加了
         * 但是用户每次登陆后检查redis大概率不会丢失数据（可能会有重复）
         */
        return to
            .concatWith(from)
            .flatMap { key ->
                message.toMono()
                    // 过滤 系统消息，这个类型的消息没必要写同步库了
                    .filter { it.creatorType != CreatorType.SYS }
                    // 写扩散
                    .flatMap { zSet.add(key, data, message.seqId.toDouble()) }
                    .flatMap {
                        redisTemplate.expire(key, Duration.ofDays(3))
                    }
            }
            .then(this)
            .flatMapMany {
                customer.flatMapIterable { cs -> cs.clientAccessServerMap.entries }
                    .concatWith(staff.flatMapIterable { staffStatus -> staffStatus.clientAccessServerMap.entries })
            }
            .filter { entry -> entry.key != messageDto.clientId }
            .map { entry -> entry.value }
            // 推送到登陆的其他客户端
            .distinct()
            .doOnNext {
                // 发送消息到kafka
                when (message.type) {
                    CreatorType.CUSTOMER -> streamBridge.send(
                        "${it}.message",
                        MessageBuilder.withPayload(customerUpdateMessage.toJson()).build()
                    )
                    CreatorType.STAFF -> streamBridge.send(
                        "${it}.message",
                        MessageBuilder.withPayload(staffUpdateMessage.toJson()).build()
                    )
                    else -> {
                    }
                }
            }
            .collectList()
            .publishOn(Schedulers.boundedElastic())
            .flatMap {
                // 持久化 到 数据库
                customer.flatMap { cs ->
                    val chatMessage = ChatMessageMapper.mapper.mapToFromMessage(messageDto.message)
                    saveMessage(ChatMessagePO(chatMessage, cs.userId))
                }
            }
            .then(customer)
                // 发送完消息进行统计
            .flatMap {
                it.statisticsByChatMessage(message)
                customerStatusService.rewriteStatus(it)
                conversationStatusService.statisticsByChatMessage(it.organizationId, it.userId, message)
            }
            .thenReturn(true)
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
                when (it.message.creatorType) {
                    // 封装客服客户 id 对，然后过滤发送消息的客户端ID
                    CreatorType.CUSTOMER -> {
                        when (it.message.type) {
                            CreatorType.STAFF -> CustomerToStaff(it.message.from, it.message.to).toMono()
                                .syncMessage(it)
                            else -> Mono.just(false)
                        }
                    }
                    CreatorType.STAFF -> {
                        when (it.message.type) {
                            CreatorType.CUSTOMER -> CustomerToStaff(it.message.to, it.message.from).toMono()
                                .syncMessage(it)
                            CreatorType.GROUP -> TODO("发送给群组")
                            CreatorType.SYS -> TODO("发送系统消息")
                            else -> Mono.just(false)
                        }
                    }
                    CreatorType.SYS -> {
                        when (it.message.type) {
                            CreatorType.CUSTOMER -> CustomerToStaff(it.message.to, null).toMono().syncMessage(it)
                            CreatorType.STAFF -> CustomerToStaff(null, it.message.to).toMono().syncMessage(it)
                            else -> Mono.just(false)
                        }
                    }
                    else -> Mono.just(false)
                }
            }
    }

    /**
     * bot 消息处理，写入到用户的 redis zSet 列表
     */
    fun syncBotMessage(message: Message): Mono<Boolean> {
        val isBot = message.creatorType == CreatorType.STAFF
        val userId = if (isBot) message.to else message.from
        val key = "msg:${message.organizationId}:${CreatorType.CUSTOMER.name.toLowerCase()}:${userId}"
        val chatMessage = ChatMessageMapper.mapper.mapToFromMessage(message)
        return Mono
            .zip(
                zSet.add(key, message.toJson(), message.seqId.toDouble()),
                saveMessage(ChatMessagePO(chatMessage, userId!!)),
                customerStatusService.findByUserId(message.organizationId!!, userId)
                    .publishOn(Schedulers.boundedElastic())
                    .flatMap {
                        // 根据消息统计一些数据
                        it.statisticsByChatMessage(message)
                        customerStatusService.rewriteStatus(it, 15, TimeUnit.MINUTES)
                        conversationStatusService.statisticsByChatMessage(it.organizationId, it.userId, message)
                            .thenReturn(true)
                    }
            )
            .map { it.t1 }
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
            .transform { Flux.zip(it, conversationStatusDtoCache) }
            .doOnNext {
                // 发送消息到kafka
                streamBridge.send("${it.t1}.conv", MessageBuilder.withPayload(it.t2.toJson()).build())
            }
            .collectList()
            .flatMap { Mono.empty() }
    }

    fun sendToAllMyCustomers(messageDto: Mono<MessageDto>) {
        val staffStatus = messageDto
            .flatMap {
                val message = it.message
                staffStatusService.findStaff(message.organizationId!!, message.from!!)
            }
            .filter { it.isOffLine() }
            .flatMapIterable { it.userIdList }
            .flatMap {
                messageDto.doOnNext { msg ->
                    msg.message.to = it
                }
            }
            .flatMap { send(it.toMono()) }
            .subscribe()
    }

    /**
     * 持久化单独消息到 cassandra
     */
    private fun saveMessage(chatMessagePO: ChatMessagePO): Mono<ChatMessagePO> {
        return conversationStatusService.findLatestByUserId(
            chatMessagePO.chatMessageKey.organizationId,
            chatMessagePO.chatMessageKey.ownerId.toLong()
        )
            .flatMap {
                chatMessagePO.conversationId = it.id
                chatMessagePORepository.save(chatMessagePO)
            }
    }
}