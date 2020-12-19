package com.qingzhu.messageserver.domain.dto

import com.qingzhu.messageserver.domain.entity.CustomerStatus

data class CustomerDispatcherDto(
        val organizationId: Int,
        val userId: Long,
        // 指定客服id
        val staffId: Long?,
        val shuntId: Long
) {
    companion object {
        fun fromCustomerStatus(customerStatus: CustomerStatus): CustomerDispatcherDto {
            return CustomerDispatcherDto(
                    organizationId = customerStatus.organizationId,
                    userId = customerStatus.userId,
                    staffId = customerStatus.staffId,
                    shuntId = customerStatus.shuntId
            )
        }
    }
}