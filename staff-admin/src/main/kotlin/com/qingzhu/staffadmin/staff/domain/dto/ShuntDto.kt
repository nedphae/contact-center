package com.qingzhu.staffadmin.staff.domain.dto

data class ShuntDto(
    /** 公司id */
    val organizationId: Int,
    // 接待组 id
    val id: Long,
    /** 接待组 名称 */
    val name: String,
    // 接待组所属分类
    /** @ManyToOne */
    val shuntClassId: Long,
    /** 接待组范围代码 */
    val code: String,
    // chat ui 配置
    var config: String?,
)