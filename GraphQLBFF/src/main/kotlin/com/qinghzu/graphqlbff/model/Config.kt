package com.qinghzu.graphqlbff.model

data class ShuntUIConfig(
    var id: Long? = null,
    /** 接待组 */
    val shuntId: Long,
    /** 界面配置，不会做 json 解析，直接通过 json-schema-validator 进行验证 */
    val config: String,
)