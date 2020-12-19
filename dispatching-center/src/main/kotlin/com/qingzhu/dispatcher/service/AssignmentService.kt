package com.qingzhu.dispatcher.service

import com.qingzhu.dispatcher.domain.dto.CustomerDispatcherDto
import com.qingzhu.dispatcher.domain.dto.CustomerInStaffServiceStatusDto
import com.qingzhu.dispatcher.domain.dto.StaffChangeStatusDto
import com.qingzhu.dispatcher.domain.dto.ConversationView
import com.qingzhu.dispatcher.service.impl.WeightedAssignmentService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class AssignmentService(
        private val messageService: MessageService,
        private val staffAdminService: StaffAdminService,
        private val weightedAssignmentService: WeightedAssignmentService
) {
    fun checkIsStaffService(organizationId: Int, uid: String): Mono<ConversationView> {
        return Mono
                .create<CustomerInStaffServiceStatusDto> {
                    // 添加 10 分钟内自动转接人工
                    it.success(messageService.checkIsStaffService(organizationId, uid))
                }
                .flatMap {
                    if (it.isStaffService) {
                        if (it.staffId != null) {
                            val staffDto = staffAdminService.getStaffInfo(organizationId, it.staffId)
                            Mono.just(staffDto!!.toStaffViewWithUserId(it.userId))
                        } else {
                            // 如果已经分配过座席
                            // 但是座席已经满，就重新分配
                            assignmentStaff(it.organizationId, it.userId)
                        }
                    } else {
                        // TODO 分配机器人，新建会话
                        Mono.empty()
                    }
                }
    }

    fun assignmentStaff(organizationId: Int, userId: Long): Mono<ConversationView> {
        val mono = Mono
                .create<CustomerDispatcherDto> {
                    val customerDispatcherDto = messageService.findStaffIdOrShuntIdOfCustomer(organizationId, userId)
                    it.success(customerDispatcherDto)
                }.cache()
        return mono
                .flatMap {
                    // 添加检查是否是在已经分配(缓存1小时客户状态)人工会话 ，如果是，就继续分配到当前客服
                    Mono.justOrEmpty(it.staffId)
                }
                .map {
                    val dto = StaffChangeStatusDto(organizationId, it!!, userId)
                    // 发送分配请求给客服 如果失败就重新分配
                    messageService.assignmentCustomer(dto)
                    dto
                }
                .onErrorResume {
                    // 分配失败就从空闲客服里面重新分配
                    Mono.empty<StaffChangeStatusDto>()
                }
                .switchIfEmpty(
                        mono.map { it.shuntId }
                                .filter { it != -1L }
                                .cache()
                                .flatMap {
                                    weightedAssignmentService.assignmentStaff(organizationId, it)
                                }
                                .map {
                                    val dto = StaffChangeStatusDto(organizationId, it!!, userId)
                                    // 发送分配请求给客服 如果失败就重新分配
                                    messageService.assignmentCustomer(dto)
                                    dto
                                }.retry(3)
                )
                .map {
                    val staffDto = staffAdminService.getStaffInfo(organizationId, it.staffId)
                    staffDto!!.toStaffViewWithUserId(it.userId)
                }
    }

}