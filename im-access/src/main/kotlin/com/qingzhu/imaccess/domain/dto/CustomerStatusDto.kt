package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.imaccess.broker.KafkaBroker
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
    val ip: String,
    /** Which server i`m in
     * 如果需要配置登陆端互提，可将 A 更改为终端类型枚举
     */
    val clientAccessServer: Pair<String, String>
) {
    companion object {
        /**
         * [clientId] 为 socket io session id
         */
        fun fromCustomerConfig(
            customerConfig: CustomerConfig,
            customerDto: CustomerDto,
            clientId: String
        ): CustomerStatusDto {
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
                ip = customerConfig.ip,
                clientAccessServer = clientId to KafkaBroker.accessServer
            )
        }
    }
}








