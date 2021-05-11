package com.qingzhu.bot.knowledgebase.domain.view

import com.qingzhu.common.message.getChatMessageSnowFlake

/**
 * 只是一个类型标注，并没有任何卵用
 * 应为 java 不支持联合类型
 */
interface Content

/**
 * 返回 给 chatui 的消息
 */

data class Message(
    val _id: String = getChatMessageSnowFlake().getNextSequenceId().toString(),
    // text,system,image,card,slot,recommend,quick-replies,cmd
    val type: String = "text",
    val content: Content,
    val createdAt: Long = System.currentTimeMillis(),
    // 'left' | 'right' | 'center';
    val position: String? = null,
    val hasTime: Boolean = false,
)

data class TextContent(
    val text: String,
) : Content

data class PicContent(
    val picUrl: String,
) : Content

data class HistoryResult(
    val lastId: Long,
    val list: List<Message>,
    val total: Int,
)
