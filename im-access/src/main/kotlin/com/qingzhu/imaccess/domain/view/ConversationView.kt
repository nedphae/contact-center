package com.qingzhu.imaccess.domain.view

/**
 * 返回给用户的客服的公开信息
 */
data class ConversationView(
        // 公司id
        val organizationId: Int,
        val conversationId: Long,
        val staffId: Long,
        val userId: Long,
        // 昵称
        val nickName: String
) {
}