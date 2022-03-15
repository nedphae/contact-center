package com.qingzhu.dispatcher.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.dispatcher.domain.constant.FromType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CustomerDispatcherDto(
    val organizationId: Int,
    val userId: Long,
    val uid: String,
    /** 指定客服id */
    val staffId: Long?,
    val groupId: Long?,
    val shuntId: Long,
    val robotShuntSwitch: Int?,
    val vipLevel: Int?,
    val fromType: FromType,
    val ip: String,
    val title: String?,
    val referrer: String?
)