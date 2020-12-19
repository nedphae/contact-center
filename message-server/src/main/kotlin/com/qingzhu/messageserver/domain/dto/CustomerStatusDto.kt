package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.messageserver.domain.entity.CustomerStatus

data class CustomerChangeStatusDto(
        // 公司id
        val organizationId: Int,
        val userId: Long
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerInStaffServiceStatusDto(
        val organizationId: Int,
        val userId: Long,
        val staffId: Long?,
        val isStaffService: Boolean
) {
    companion object {
        fun fromCustomerStatus(customerStatus: CustomerStatus): CustomerInStaffServiceStatusDto {
            return CustomerInStaffServiceStatusDto(
                    organizationId = customerStatus.organizationId,
                    userId = customerStatus.userId,
                    staffId = customerStatus.staffId,
                    isStaffService = customerStatus.isStaffService
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerStatusDto(
        // 公司id
        val organizationId: Int,
        // 客户系统id
        val userId: Long,
        // 客户提交id
        val uid: String?,
        // 自定义访客咨询来源页的标题，不配置sdk会自动抓取, 和referrer一起使用
        val title: String?,
        // 自定义访客咨询来源页的url，不配置sdk会自动抓取，和title一起使用
        val referrer: String?,
        // 指定客服id
        val staffId: Long?,
        // 指定客服组id
        val groupId: Long?,
        // 访客选择多入口分流模版id
        val shuntId: Long,
        // 机器人优先开关（访客分配）
        val robotShuntSwitch: Int?,
        // 客服所处服务器 hash 值
        var redisHashKey: Int
) {
    fun toCustomerStatus(): CustomerStatus {
        return CustomerStatus(
                organizationId = this.organizationId,
                userId = this.userId,
                uid = this.uid,
                title = this.title,
                referrer = this.referrer,
                staffId = this.staffId,
                groupId = this.groupId,
                shuntId = this.shuntId,
                robotShuntSwitch = this.robotShuntSwitch,
                redisHashKey = this.redisHashKey
        )
    }
}