package com.qingzhu.imaccess.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.imaccess.domain.query.CustomerConfig

data class CustomerBaseStatusDto(
        // 公司id
        val organizationId: Int,
        val userId: Long
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerStatusDto(
        // 公司id
        val organizationId: Int,
        // 客户id
        val userId: Long,

        val uid: String?,
        // 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用
        val title: String?,
        // 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用
        val referrer: String?,
        // 访客选择多入口分流模版id
        val shuntId: Long,
        // 指定客服id
        val staffId: Long?,
        // 指定客服组id
        val groupId: Long?,
        // 机器人优先开关（访客分配）
        val robotShuntSwitch: Int?
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
                    robotShuntSwitch = customerConfig.robotShuntSwitch
            )
        }
    }
}








