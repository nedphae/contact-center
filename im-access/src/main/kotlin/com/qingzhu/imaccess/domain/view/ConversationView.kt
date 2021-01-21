package com.qingzhu.imaccess.domain.view

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

/**
 * 返回给用户的客户的会话信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConversationView(
		val id: Long?,
		/** 公司id */
		val organizationId: Int,
		/** 客服id */
		val staffId: Long?,
		val userId: Long,
		val nickName: String?,
		/** 0=客服正常会话  1=机器人会话 */
		val interaction: Int?,
		/** 会话结束时间 */
		val endTime: LocalDateTime?,
		/** 当前排队信息 */
		val queue: Long?
)