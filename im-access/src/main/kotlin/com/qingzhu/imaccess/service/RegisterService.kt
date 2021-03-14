package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.query.StaffConfig
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

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
        return staffAdminService
            .getReceptionistGroup(staffConfig.organizationId!!, staffConfig.staffId!!)
            .transformDeferredContextual { t, u ->
                t.map { StaffStatusDto.fromStaffConfigAndStaff(staffConfig, it, u["clientId"]) }
            }
                .transform { dto -> messageService.registerStaff(dto).transform { dto } }
    }

    /**
     * 注册客户在线信息
     */
    fun registerCustomer(customerConfig: CustomerConfig): Mono<CustomerStatusDto> {
        val customerDto = CustomerDto.fromCustomerConfig(customerConfig)
        // 客户信息现在保存到了 调度服务器 TODO: 后期再拆分到单独的服务器
        return dispatchingCenter.updateCustomer(customerDto.toMono())
            .transformDeferredContextual { t, u ->
                t.map { CustomerStatusDto.fromCustomerConfig(customerConfig, it, u["clientId"]) }
            }
                // 注册信息
                .transform { dto -> messageService.registerCustomer(dto).transform { dto } }
    }

    /**
     * 注册客服在线信息
     */
    fun unRegisterStaff(organizationId: Int, staffId: Long) {
        messageService
                .unregisterStaff(StaffChangeStatusDto(organizationId, staffId).toMono())
                .retry(3)
                .subscribe()
    }

    /**
     * 注册客户在线信息
     */
    fun unRegisterCustomer(organizationId: Int, userId: Long) {
        messageService
                .unregisterCustomer(CustomerBaseStatusDto(organizationId, userId).toMono())
                .retry(3)
                .subscribe()
    }

}