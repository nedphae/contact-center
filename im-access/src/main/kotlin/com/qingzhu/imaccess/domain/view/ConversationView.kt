package com.qingzhu.imaccess.domain.view

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import java.time.Instant

/**
 * 返回给用户的客户的会话信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConversationView(
    val id: Long? = null,
    /** 公司id */
    val organizationId: Int? = null,
    /** 客服id 如果不需要转人工就为空 */
    val staffId: Long? = null,
    val userId: Long? = null,
    val shuntId: Long? = null,
    val nickName: String? = null,
    /** 0=客服正常会话  1=机器人会话 */
    val interaction: Int? = null,
    /** 会话结束时间 */
    val endTime: Instant? = null,
    /** 当前排队信息 */
    val queue: Long? = null,
    var blockOnStaff: Int = 0,

    val errorCode: Int? = null,
    val errorMessage: String? = null,

    /** 接待组范围代码 */
    var config: String? = null,

    var historyMsg: List<ChatUIMessage>? = null,
)