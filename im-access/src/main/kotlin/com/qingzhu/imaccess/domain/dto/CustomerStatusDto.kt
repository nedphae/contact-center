package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.imaccess.broker.KafkaBroker
import com.qingzhu.imaccess.domain.constant.FromType
import com.qingzhu.imaccess.domain.query.CustomerConfig

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerBaseStatusDto(
    /** 公司id */
    val organizationId: Int,
    val userId: Long,
    val terminator: CreatorType? = null,
    val clientAccessServer: String? = null,
)

data class CustomerBaseClientDto(
    /** 公司id */
    val organizationId: Int,
    val userId: Long,
    /** Which server i`m in
     * 如果需要配置登陆端互提，可将 A 更改为终端类型枚举
     */
    val clientAccessServer: Pair<String, String>
) {
    /**
     * [clientId] 为 socket io session id
     */
    constructor(organizationId: Int, userId: Long, clientId: String) :
            this(organizationId, userId, clientId to KafkaBroker.accessServer)
}

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
) {
    companion object {
        fun fromCustomerConfig(
            customerConfig: CustomerConfig,
            customerDto: CustomerDto,
            shuntDto: ShuntDto,
        ): CustomerStatusDto {
            return CustomerStatusDto(
                organizationId = shuntDto.organizationId,
                userId = customerDto.userId!!,
                uid = customerConfig.uid,
                title = customerConfig.title,
                referrer = customerConfig.referrer,
                shuntId = shuntDto.id,
                staffId = customerConfig.staffId,
                groupId = customerConfig.groupId,
                robotShuntSwitch = customerConfig.robotShuntSwitch,
                vipLevel = customerConfig.vipLevel,
                fromType = customerConfig.fromType,
                ip = customerConfig.ip,
            )
        }
    }
}








