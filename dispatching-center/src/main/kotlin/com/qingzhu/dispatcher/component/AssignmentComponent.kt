package com.qingzhu.dispatcher.component

import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.component.impl.WeightedAssignmentService
import com.qingzhu.dispatcher.domain.dto.*
import com.qingzhu.dispatcher.domain.entity.UserQueue
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
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
    private val listOps: ReactiveListOperations<String, String> = redisTemplate.opsForList()

    /**
     * 根据 机构id[organizationId] 和用户id[userId] 获取用户在状态服务器的调度相关信息
     */
    fun getCustomerDispatcherWithCache(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
                .cache()
                .filter { it.shuntId != -1L }
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
     */
    fun saveConversation(conversationStatusDto: Mono<ConversationStatusDto>): Mono<ConversationStatusDto> {
        return conversationStatusDto
                .transform { messageService.saveConversation(it) }
    }

    /**
     * 通过 [staffChangeStatusDto] 调用客服状态服务器分配座席信息，成功就发送分配消息到消息服务器
     * 消息服务器会发送消息给客服
     */
    fun assignmentCustomerAndSendEvent(staffChangeStatusDto: Mono<StaffChangeStatusDto>): Mono<Unit> {
        return messageService.assignmentCustomer(staffChangeStatusDto)
                .doOnNext {
                    // TODO 发送分配信号
                }
    }

    /**
     * 根据 机构id[organizationId] 和 用户id[userId] 分配机器人会话
     */
    fun getBot(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleBotStaff(organizationId, it) }
    }

    /**
     * 根据 机构id[organizationId] 和 用户id[userId] 分配机器人会话
     */
    fun getStaff(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleStaff(organizationId, it) }
    }

    /**
     * 把机构 [organizationId] 对应的 [userId] 放入列队里面
     */
    fun pushIntoQueue(organizationId: Int, userId: Long): Mono<ConversationViewDto> {
        // staff 方法获取客服为空时 进行排队
        return Mono
                .just(userId)
                .flatMap {
                    // 客户 插入到 redis 列队
                    listOps.leftPush("queue:$organizationId", UserQueue(organizationId, userId).toJson())
                }
                .map { ConversationViewDto(organizationId, userId, it) }
    }

    /**
     * 根据 机构id[organizationId] 和 用户id[userId]，并调用 [getStaffList] 获取客服列表，然后
     * 根据列表调用分配算法分配客服
     */
    private fun assignment(organizationId: Int, userId: Long,
                           getStaffList: (shuntId: Long) ->
                           Flux<StaffDispatcherDto>): Mono<ConversationStatusDto> {
        val customerDispatcherDto = getCustomerDispatcherWithCache(organizationId, userId)
        return customerDispatcherDto
                .map { getStaffList(it.shuntId) }
                .cache()
                .flatMap {
                    // 根据权重分配客服
                    weightedAssignmentService.assignmentStaff(it)
                }
                .flatMap {
                    val dto = StaffChangeStatusDto(organizationId, it!!, userId).toMono()
                    // 发送分配请求给客服 如果失败就重新分配
                    assignmentCustomerAndSendEvent(dto).then(dto)
                }
                .retry(3)
                .flatMap {
                    staffAdminService.getStaffInfo(it.organizationId, it.staffId)
                }
                .flatMap {
                    customerDispatcherDto
                            .map { cd ->
                                ConversationStatusDto.fromStaffAndCustomer(it!!,
                                        // it.staffType.inv()
                                        // 会话类型 和 客服类型相同
                                        cd!!)
                            }
                }
    }
}