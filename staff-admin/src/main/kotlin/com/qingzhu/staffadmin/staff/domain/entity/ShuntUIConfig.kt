package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

/**
 * 聊天界面 UI 配置
 */
@Table
data class ShuntUIConfig(
        /** 公司id */
        val organizationId: Int,
        /** 接待组 */
        val shuntId: Long,
        /** 界面配置，不会做 json 解析，直接通过 json-schema-validator 进行验证 */
        val config: String,
) : AbstractAuditingEntity() {
        @Id
        var id: Long? = null
}
