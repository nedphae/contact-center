package com.qingzhu.imaccess.service

import com.qingzhu.common.security.AuthorizedFeignClient
import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.domain.view.ConversationView
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * TODO 后续使用 webflux webclient 优化？
 */
@Service
@AuthorizedFeignClient(name = "dispatching-center")
interface DispatchingCenter {
    @PostMapping(value = ["/customer"])
    fun updateCustomer(customerDto: CustomerDto): CustomerDto?

    @PutMapping(value = ["/assignment/auto"])
    fun assignmentAuto(@RequestParam("organizationId") organizationId: Int, @RequestParam("userId") userId: Long): ConversationView?

    @PutMapping(value = ["/assignment/staff"])
    fun assignmentStaff(@RequestParam("organizationId") organizationId: Int, @RequestParam("userId") userId: Long): ConversationView?
}

@Service
@AuthorizedFeignClient(name = "message-server")
interface MessageService {
    @PostMapping(value = ["/message/send"])
    fun send(message: Message)

    @PostMapping(value = ["/register/customer"])
    fun registerCustomer(customerDto: CustomerStatusDto)

    @PutMapping(value = ["/unregister/customer"])
    fun unregisterCustomer(customerDto: CustomerBaseStatusDto)

    @PostMapping(value = ["/register/staff"])
    fun registerStaff(staffStatusDto: StaffStatusDto)

    @PutMapping(value = ["/unregister/staff"])
    fun unregisterStaff(staffChangeStatusDto: StaffChangeStatusDto)

    @GetMapping(value = ["/status/customer/find-by-uid"])
    fun findCustomerByUid(@RequestParam("organizationId") organizationId: Int, @RequestParam("uid") uid: String): CustomerBaseStatusDto?
}

@Service
@AuthorizedFeignClient(name = "staff-admin")
interface StaffAdminService {
    @GetMapping(value = ["/staff/receptionist"])
    fun getReceptionistGroup(@RequestParam("organizationId") organizationId: Int, @RequestParam("staffId") staffId: Long): ReceptionistGroupDto?
}