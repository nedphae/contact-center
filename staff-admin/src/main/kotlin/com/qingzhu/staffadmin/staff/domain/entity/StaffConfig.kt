package com.qingzhu.staffadmin.staff.domain.entity

import com.qingzhu.common.domain.AbstractAuditingEntity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table
data class StaffConfig(
		@Id
		var id: Long? = null,
		/** 公司id */
		val organizationId: Int,
		// 配置的客服 (每个客服可以有多个配置)
		/** @ManyToOne */
		val staffId: Long,
		/** 接待组 id */
		val shuntId: Long,
		/** 配置优先级 */
		var priority: Int
) : AbstractAuditingEntity()