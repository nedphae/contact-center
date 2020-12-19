package com.qingzhu.dispatcher.domain.dto


/**
 * 返回给用户的客服的公开信息
 */
data class ConversationView(
        val id: Long,
        // 公司id
        val organizationId: Int,
        val staffId: Long,
        val userId: Long,
        // 昵称
        val nickName: String
) {
}