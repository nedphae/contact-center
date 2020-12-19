package com.qingzhu.messageserver.domain.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.messageserver.domain.constant.CreatorType
import com.qingzhu.messageserver.domain.value.Content
import java.time.LocalDateTime

/**
 * 消息请求
 * 参考了钉钉/微信web的消息结构设计
 * 但是大部分都不一样
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Message(
        // 公司id
        val organizationId: Int,
        /**
         * 客户端生成唯一id (去重使用)
         * uuid 为聊天消息去重
         * [com.qingzhu.common.message.Header.mid] 为服务器消息去重
         */
        val uuid: String,

        val seqId: Long,

        val createdAt: LocalDateTime,
        // 会话id
        val conversationId: Long,
        // 消息来源 (服务器设置)
        var from: Long,
        // 消息送至
        var to: Long,
        // 消息类型 接收者类型
        val type: CreatorType,
        // 创建者类型
        val creatorType: CreatorType,
        // 内容
        val content: Content,
        // 昵称
        val nickName: String? = null
) {
}