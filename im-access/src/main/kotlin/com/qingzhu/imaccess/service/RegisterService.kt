package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.query.StaffConfig
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

/**
 * 注册服务
 */
@Service
class RegisterService(
        private val dispatchingCenter: DispatchingCenter,
        private val staffAdminService: StaffAdminService,
        private val messageService: MessageService
) {

    /**
     * 注册客服在线信息
     */
    fun registerStaff(staffConfig: StaffConfig): Mono<StaffStatusDto> {
        val receptionistGroupDto = staffAdminService.getReceptionistGroup(staffConfig.organizationId!!, staffConfig.staffId!!)
        return Mono.justOrEmpty(receptionistGroupDto)
                .map { StaffStatusDto.fromStaffConfigAndStaff(staffConfig, it!!) }
                .doOnSuccess { messageService.registerStaff(it) }
    }

    /**
     * 注册客户在线信息
     */
    fun registerCustomer(customerConfig: CustomerConfig): Mono<CustomerStatusDto> {
        val customerDto = CustomerDto.fromCustomerConfig(customerConfig)
        return Mono.justOrEmpty(dispatchingCenter.updateCustomer(customerDto))
                .map { CustomerStatusDto.fromCustomerConfig(customerConfig, it!!) }
                // 注册信息
                .doOnSuccess { messageService.registerCustomer(it) }
    }

    /**
     * 注册客服在线信息
     */
    fun unRegisterStaff(organizationId: Int, staffId: Long) {
        messageService.unregisterStaff(StaffChangeStatusDto(organizationId, staffId))
    }

    /**
     * 注册客户在线信息
     */
    fun unRegisterCustomer(organizationId: Int, userId: Long) {
        messageService.unregisterCustomer(CustomerChangeStatusDto(organizationId, userId))
    }

}