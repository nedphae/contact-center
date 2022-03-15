package com.qingzhu.dispatcher.component

import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.component.impl.WeightedAssignmentService
import com.qingzhu.dispatcher.domain.dto.*
import com.qingzhu.dispatcher.domain.entity.UserQueue
import org.springframework.data.redis.core.ReactiveHashOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class AssignmentComponent(
    private val messageService: MessageService,
    private val staffAdminService: StaffAdminService,
    private val weightedAssignmentService: WeightedAssignmentService,
    redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val hashOps: ReactiveHashOperations<String, String, String> by lazy { redisTemplate.opsForHash() }

    /**
     * 根据 机构id[organizationId] 和用户id[userId] 获取用户在状态服务器的调度相关信息
     */
    fun getCustomerDispatcherWithCache(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
            .cache()
    }

    /**
     * 通过 机构id[organizationId] 和 用户id[userId] 获取上缓存服务器上的会话信息
     */
    fun getLastConversationWithCache(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return messageService.findConversationByUserId(organizationId, userId)
            .cache()
    }

    /**
     * 设置会话结束
     */
    fun endConversation(conversationStatusDto: Mono<ConversationStatusDto>): Mono<Unit> {
        return conversationStatusDto
            .transform { messageService.endConversation(it) }
    }

    /**
     * 更新会话信息
     * 然后 发送分配消息到消息服务器
     * 消息服务器会发送消息给客服
     */
    fun saveConversation(conversationStatusDto: Mono<ConversationStatusDto>): Mono<ConversationStatusDto> {
        val conversationStatusWithId = conversationStatusDto
            .doOnNext {
                it.closeReason = null
                it.endTime = null
            }
            .transform {
                messageService.saveConversation(it)
            }.cache()
        return conversationStatusWithId
            .filter { cov -> cov.interaction == 1 }
            .flatMap {
                val mono = it.toMono()
                messageService.sendAssignmentSignal(mono).then(mono)
            }
            .switchIfEmpty(conversationStatusWithId)
    }

    /**
     * 通过 [staffChangeStatusDto] 调用客服状态服务器分配座席信息，成功就返回然后新建会话信息
     */
    fun assignmentCustomer(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<ResponseEntity<Void>> {
        return messageService.assignmentCustomer(staffChangeStatusDto)
    }

    /**
     * 根据 [customerDispatcherDto] 用户的接待组信息等 分配机器人会话
     */
    fun getBot(customerDispatcherDto: Mono<CustomerDispatcherDto>): Mono<ConversationStatusDto> {
        return assignment(customerDispatcherDto) { messageService.findIdleBotStaff(it.organizationId, it.shuntId) }
    }

    /**
     * 根据 [customerDispatcherDto] 用户的接待组信息等 分配人工会话
     */
    fun getStaff(customerDispatcherDto: Mono<CustomerDispatcherDto>): Mono<ConversationStatusDto> {
        return assignment(customerDispatcherDto) { messageService.findIdleStaff(it.organizationId, it.shuntId) }
    }

    /**
     * 把机构 [customerDispatcherDto] 对应的 客户 放入列队里面
     */
    fun pushIntoQueue(customerDispatcherDto: Mono<CustomerDispatcherDto>): Mono<ConversationViewDto> {
        // staff 方法获取客服为空时 进行排队
        return customerDispatcherDto
            .flatMap {
                // 客户 插入到 redis 列队
                hashOps.put(
                    "queue:${it.shuntId}",
                    it.userId.toString(),
                    UserQueue(it.organizationId, it.shuntId, it.userId).toJson()
                ).flatMap { _ -> Mono.zip(hashOps.size("queue:${it.shuntId}"), it.toMono()) }
            }
            .map { ConversationViewDto(it.t2.organizationId, it.t2.userId, it.t2.shuntId, it.t1) }
    }

    fun createConversation(
        customerDispatcherDto: CustomerDispatcherDto,
        staffId: Long
    ): Mono<ConversationStatusDto> {
        return staffAdminService.getStaffInfo(customerDispatcherDto.organizationId, staffId)
            .map {
                ConversationStatusDto.fromStaffAndCustomer(
                    it,
                    // it.staffType.inv()
                    // 会话类型 和 客服类型相同
                    customerDispatcherDto
                )
            }
    }

    /**
     * 根据 [customerDispatcherDto]，并调用 [getStaffList] 获取客服列表，然后
     * 根据列表调用分配算法分配客服
     */
    private fun assignment(
        customerDispatcherDto: Mono<CustomerDispatcherDto>,
        getStaffList: (customerDispatcherDto: CustomerDispatcherDto) ->
        Flux<StaffDispatcherDto>
    ): Mono<ConversationStatusDto> {
        return customerDispatcherDto
            .map { getStaffList(it) }
            .cache()
            .flatMap {
                // 根据权重分配客服
                Mono.zip(weightedAssignmentService.assignmentStaff(it), customerDispatcherDto)
            }
            .flatMap {
                val dto = StaffChangeStatusDto(it.t2.organizationId, it.t1, it.t2.userId).toMono()
                // 发送分配请求给客服 如果失败就重新分配
                assignmentCustomer(dto).then(dto)
            }
            // .retry(3)
            .flatMap {
                customerDispatcherDto
                    .flatMap { cdd ->
                        createConversation(cdd, it.staffId)
                    }
            }
    }
}