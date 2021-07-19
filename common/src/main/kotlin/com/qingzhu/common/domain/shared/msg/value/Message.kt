package com.qingzhu.common.domain.shared.msg.value

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.message.getChatMessageSnowFlake
import java.time.Instant
import java.util.*

/**
 * 消息请求
 * 参考了钉钉/微信web的消息结构设计
 * 但是大部分都不一样
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Message(
    /** 公司id */
    var organizationId: Int? = null,
    /**
     * 客户端生成唯一id (去重使用)
     * uuid 为聊天消息去重
     * [com.qingzhu.common.message.Header.mid] 为服务器消息去重
     */
    val uuid: String = UUID.randomUUID().toString(),
    val seqId: Long = getChatMessageSnowFlake().getNextSequenceId(),
    val createdAt: Instant = Instant.now(),
    /** 会话id */
    var conversationId: Long?,
    /** 消息来源 (服务器设置) */
    var from: Long? = null,
    /** 消息送至 */
    var to: Long? = null,
    /** 消息类型 接收者类型 */
    val type: CreatorType,
    /** 创建者类型 */
    val creatorType: CreatorType,
    /** 内容 */
    val content: Content,
    /** 昵称 */
    val nickName: String? = null
)