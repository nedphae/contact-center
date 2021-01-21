package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.imaccess.domain.constant.FromType
import com.qingzhu.imaccess.domain.query.CustomerConfig

data class CustomerBaseStatusDto(
        /** 公司id */
        val organizationId: Int,
        val userId: Long
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerStatusDto(
        val organizationId: Int,
        val userId: Long,
        val uid: String,
        val title: String?,
        val referrer: String?,
        val shuntId: Long,
        val staffId: Long?,
        val groupId: Long?,
        val robotShuntSwitch: Int?,
        val vipLevel: Int?,
        val fromType: FromType,
        val ip: String
) : BaseDto() {
    companion object {
        fun fromCustomerConfig(customerConfig: CustomerConfig, customerDto: CustomerDto): CustomerStatusDto {
            return CustomerStatusDto(
                    organizationId = customerConfig.organizationId,
                    userId = customerDto.userId!!,
                    uid = customerConfig.uid,
                    title = customerConfig.title,
                    referrer = customerConfig.referrer,
                    shuntId = customerConfig.shuntId,
                    staffId = customerConfig.staffId,
                    groupId = customerConfig.groupId,
                    robotShuntSwitch = customerConfig.robotShuntSwitch,
                    vipLevel = customerConfig.vipLevel,
                    fromType = customerConfig.fromType,
                    ip = customerConfig.ip
            )
        }
    }
}








