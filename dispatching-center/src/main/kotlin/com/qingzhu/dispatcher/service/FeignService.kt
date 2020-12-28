package com.qingzhu.dispatcher.service

import com.qingzhu.common.security.AuthorizedFeignClient
import com.qingzhu.dispatcher.domain.dto.*
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

@Service
@AuthorizedFeignClient(name = "message-server")
interface MessageService {
    @GetMapping(value = ["/status/staff/idle"])
    fun findIdleStaff(@RequestParam("organizationId") organizationId: Int,
                      @RequestParam("shuntId") shuntId: Long): List<StaffDispatcherDto>

    @PutMapping(value = ["/status/staff/assignment"])
    fun assignmentCustomer(staffChangeStatusDto: StaffChangeStatusDto)

    @GetMapping(value = ["/status/customer/shunt-id"])
    fun findStaffIdOrShuntIdOfCustomer(@RequestParam("organizationId") organizationId: Int, @RequestParam("userId") userId: Long): CustomerDispatcherDto?

    @GetMapping(value = ["/status/conversation/find-by-user-id"])
    fun findConversationByUserId(@RequestParam("organizationId") organizationId: Int,
                                 @RequestParam("userId") userId: Long): ConversationView?

    @GetMapping(value = ["/status/staff/bot/idle"])
    fun findIdleBotStaff(@RequestParam("organizationId") organizationId: Int,
                         @RequestParam("shuntId") shuntId: Long): List<StaffDispatcherDto>

    @PostMapping(value = ["/status/conversation/new"])
    fun createConversation(conversationStatusDto: ConversationStatusDto): ConversationView?

    @PutMapping(value = ["/status/conversation/end"])
    fun endConversation(conversationEndDto: ConversationEndDto)
}

@Service
@AuthorizedFeignClient(name = "staff-admin")
interface StaffAdminService {
    @GetMapping(value = ["/staff/info"])
    fun getStaffInfo(@RequestParam("organizationId") organizationId: Int, @RequestParam("staffId") staffId: Long): StaffDto?
}