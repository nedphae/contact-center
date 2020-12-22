package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.dto.*
import com.qingzhu.dispatcher.service.impl.WeightedAssignmentService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AssignmentService(
        private val messageService: MessageService,
        private val staffAdminService: StaffAdminService,
        private val weightedAssignmentService: WeightedAssignmentService
) {
    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = Mono
                .create<ConversationView> {
                    // 添加 10 分钟内自动转接人工
                    it.success(messageService.findConversationByUserId(organizationId, userId))
                }.cache()
        return mono
                .doOnSuccess {
                    // 尝试分配到历史客服
                    messageService.assignmentCustomer(StaffChangeStatusDto(it.organizationId, it.staffId, it.userId))
                }
                .onErrorResume { mono }
                .flatMap {
                    if (it.interaction == 0) {
                        // 客服会话
                        staff(it.organizationId, it.userId)
                    } else {
                        // 机器人会话
                        bot(it.organizationId, it.userId)
                    }
                }
                .doOnSuccess {
                    // 进行会话关联

                }
                // 不存在历史会话
                .switchIfEmpty(bot(organizationId, userId))
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
                .doOnSuccess {
                    // 设置会话状态

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


    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        return assignment(organizationId, userId) { messageService.findIdleStaff(organizationId, it) }
                .
    }
}