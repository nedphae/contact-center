package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.constant.RelatedType
import com.qingzhu.dispatcher.domain.constant.TransferType
import com.qingzhu.dispatcher.domain.dto.*
import com.qingzhu.dispatcher.service.impl.WeightedAssignmentService
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Duration

@Service
class AssignmentService(
        private val messageService: MessageService,
        private val staffAdminService: StaffAdminService,
        private val weightedAssignmentService: WeightedAssignmentService,
        redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val listOps: ReactiveListOperations<String, String> = redisTemplate.opsForList()

    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = Mono
                .create<ConversationView> {
                    it.success(messageService.findConversationByUserId(organizationId, userId))
                }.cache()
        // 添加 10 分钟内自动转接人工
        return mono
                .doOnSuccess {
                    // 尝试分配到历史客服
                    messageService.assignmentCustomer(StaffChangeStatusDto(it.organizationId, it.staffId!!, it.userId))
                }
                .onErrorResume { mono }
                .flatMap {
                    if (it.interaction == 0) {
                        // 客服会话
                        staff(it.organizationId, it.userId)
                                .doOnSuccess { cs ->
                                    // 设置为主动转人工
                                    cs.transferType = TransferType.INITIATIVE
                                }
                    } else {
                        // 机器人会话
                        bot(it.organizationId, it.userId)
                    }
                }
                // 不存在历史会话 或者 重新分配客服失败 就转到机器人客服
                .switchIfEmpty(bot(organizationId, userId))
                .flatMap {
                    // 进行会话关联
                    mono.map { cv ->
                        it.relatedId = cv.id
                        it.relatedType = RelatedType.FROM_HISTORY
                        it.visitRange = Duration.between(it.startTime, cv.endTime).toMillis()
                        it
                    }
                }
                .flatMap {
                    Mono.justOrEmpty(messageService.createConversation(it))
                }
    }

    private fun bot(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleBotStaff(organizationId, it) }
    }

    private fun assignment(organizationId: Int, userId: Long,
                           getStaffList: (shuntId: Long) ->
                           List<StaffDispatcherDto>): Mono<ConversationStatusDto> {
        val customerDispatcherDto = getCustomerDispatcher(organizationId, userId)
        return customerDispatcherDto
                .map { getStaffList(it.shuntId) }
                .cache()
                .flatMap {
                    // 根据权重分配客服
                    weightedAssignmentService.assignmentStaff(it)
                }
                .map {
                    val dto = StaffChangeStatusDto(organizationId, it!!, userId)
                    // 发送分配请求给客服 如果失败就重新分配
                    messageService.assignmentCustomer(dto)
                    dto
                }.retry(3)
                .flatMap {
                    Mono.justOrEmpty(staffAdminService.getStaffInfo(it.organizationId, it.staffId))
                }
                .flatMap {
                    customerDispatcherDto
                            .map { cd ->
                                ConversationStatusDto.fromStaffAndCustomer(it!!,
                                        cd!!, 0)
                            }
                }
    }

    private fun getCustomerDispatcher(organizationId: Int, userId: Long): Mono<CustomerDispatcherDto> {
        return Mono.create<CustomerDispatcherDto> {
            val customerDispatcherDto = messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
            it.success(customerDispatcherDto)
        }
                .cache()
                .filter { it.shuntId != -1L }
    }

    private fun staff(organizationId: Int, userId: Long): Mono<ConversationStatusDto> {
        return assignment(organizationId, userId) { messageService.findIdleStaff(organizationId, it) }
    }


    /**
     * 手动转人工
     */
    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = Mono
                .create<ConversationStatusDto> {
                    // 通过 JPA 获取历史会话的座席信息
                    it.success()
                }.cache()
        return mono
                .doOnSuccess {
                    // 尝试分配到历史客服
                    messageService.assignmentCustomer(StaffChangeStatusDto(it.organizationId, it.staffId, it.userId))
                }
                .onErrorResume {
                    // 分配失败就重新分配到其他客服
                    staff(organizationId, userId)
                }
                .doOnSuccess {
                    // TODO: 与机器人会话 进行关联
                }
                .flatMap {
                    // null 的时候丢失了类型信息
                    Mono.justOrEmpty<ConversationView>(messageService.createConversation(it))
                }
                .switchIfEmpty {
                    // staff 方法获取客服为空时 进行排队
                    Mono
                            .just(userId)
                            .flatMap {
                                // 客户 插入到 redis 列队
                                listOps.leftPush("queue:$organizationId", it.toString())
                            }
                            .map { ConversationView(organizationId, userId, it) }
                }
    }
}