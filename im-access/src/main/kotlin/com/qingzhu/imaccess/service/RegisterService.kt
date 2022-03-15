package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.query.CustomerConfig
import com.qingzhu.imaccess.domain.query.StaffConfig
import org.springframework.http.HttpStatus
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
                t.map {
                    val dto = StaffStatusDto.fromStaffConfigAndStaff(staffConfig, it, u["clientId"])
                    // note: 之前的 两个 transform 会不执行中间的 registerStaff 动作，因为 transform 把 reactor 对象自身传递过去
                    messageService.registerStaff(dto.toMono()).subscribe()
                    dto
                }
            }

    }

    /**
     * 注册客户在线信息
     */
    fun registerCustomer(customerConfig: CustomerConfig, shuntDto: ShuntDto): Mono<CustomerStatusDto> {
        val customerDto = CustomerDto.fromCustomerConfig(customerConfig, shuntDto)
        // 客户信息现在保存到了 调度服务器
        // TODO: 后期再拆分到单独的服务器
        return dispatchingCenter.updateCustomer(customerDto.toMono())
            .map {
                val dto = CustomerStatusDto.fromCustomerConfig(customerConfig, it, shuntDto)
                // 注册信息
                messageService.registerCustomer(dto.toMono()).subscribe()
                dto
            }
    }

    /**
     * 注销客服在线信息
     */
    fun unRegisterStaff(organizationId: Int, staffId: Long, clientId: String) {
        messageService
            .unregisterStaff(StaffChangeStatusDto(organizationId, staffId, clientId).toMono())
            .retry(3)
            .subscribe()
    }

    /**
     * 注销客户在线信息
     */
    fun unRegisterCustomer(organizationId: Int, userId: Long, terminator: CreatorType = CreatorType.CUSTOMER): Mono<CustomerStatusDto> {
        return messageService
            .unregisterCustomer(CustomerBaseStatusDto(organizationId, userId, terminator).toMono())
            .retry(3)
            .filter { it.statusCode == HttpStatus.ACCEPTED }
            .flatMap { csd ->
                dispatchingCenter.assignmentFromQueue(organizationId, userId)
                    .then(csd.body.toMono())
            }
    }

}