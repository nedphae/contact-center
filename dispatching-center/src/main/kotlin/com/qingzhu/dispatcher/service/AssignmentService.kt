package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.dto.CustomerDispatcherDto
import com.qingzhu.dispatcher.domain.dto.StaffChangeStatusDto
import com.qingzhu.dispatcher.domain.dto.ConversationView
import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
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
                        assignmentStaff(it.organizationId, it.userId)
                    } else {
                        // 机器人会话
                        assignmentBot(it.organizationId, it.userId)
                    }
                }
                .doOnSuccess {
                    // 进行会话关联

                }
                // 不存在历史会话
                .switchIfEmpty(assignmentBot(organizationId, userId))
    }

    private fun assignmentBot(organizationId: Int, userId: Long): Mono<ConversationView> {
        return getCustomerDispatcher(organizationId, userId)
                .map { messageService.findIdleBotStaff(organizationId, it) }
                .transform {
                    assignment(it, organizationId, userId)
                }
    }

    private fun assignment(staffList: Mono<List<StaffDispatcherDto>>, organizationId: Int, userId: Long): Mono<ConversationView> {
        return staffList
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
                .map {
                    val staffDto = staffAdminService.getStaffInfo(it.organizationId, it.staffId)
                    // TODO 根据 user 和 staff 创建会话
                    staffDto!!.toStaffViewWithUserId(it.userId)
                }
    }

    private fun getCustomerDispatcher(organizationId: Int, userId: Long): Mono<Long> {
        return Mono.create<CustomerDispatcherDto> {
            val customerDispatcherDto = messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
            it.success(customerDispatcherDto)
        }
                .cache()
                .map { it.shuntId }
                .filter { it != -1L }
    }

    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        return getCustomerDispatcher(organizationId, userId)
                .map { messageService.findIdleStaff(organizationId, it) }
                .transform {
                    assignment(it, organizationId, userId)
                }
    }

}