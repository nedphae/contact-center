package com.qingzhu.dispatcher.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerInStaffServiceStatusDto(
        val organizationId: Int,
        val userId: Long,
        val staffId: Long?,
        val isStaffService: Boolean
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerDispatcherDto(
        val organizationId: Int,
        val userId: Long,
        // 指定客服id
        val staffId: Long?,
        val shuntId: Long
) {
}