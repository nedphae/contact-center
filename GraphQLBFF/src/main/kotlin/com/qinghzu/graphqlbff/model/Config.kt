package com.qinghzu.graphqlbff.model

import com.fasterxml.jackson.annotation.JsonInclude

data class ShuntUIConfig(
    var id: Long? = null,
    /** 接待组 */
    val shuntId: Long,
    /** 界面配置，不会做 json 解析，直接通过 json-schema-validator 进行验证 */
    val config: String,
)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Properties(
    // var organizationId: Int? = null,
    // var staffId: Long? = null,
    val id: Int?,
    val key: String?,
    var value: String?,
    var label: String?,
    /** 是否启用 */
    var available: Boolean? = true,
    /** 是系统/个人配置 */
    var personal: Boolean? = false,
) : AbstractStaffEntity()