package com.qingzhu.bot.knowledgebase.domain.view

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.message.getChatMessageSnowFlake

/**
 * 只是一个类型标注，并没有任何卵用
 * 应为 java 不支持联合类型
 */
/**
 * 返回 给 chatui 的消息
 */
data class ChatUIMessage(
    val _id: String = getChatMessageSnowFlake().getNextSequenceId().toString(),
    // text,system,image,card {code: promotion, slot,recommend,quick-replies} ,cmd
    val type: String = "text",
    val content: ChatUIContent,
    val createdAt: Long = System.currentTimeMillis(),
    // 'left' | 'right' | 'center';
    val position: String? = null,
    val hasTime: Boolean = false,
) {
    companion object {
        fun fromMessage(message: Message) : ChatUIMessage {
            return ChatUIMessage(
                _id = message.seqId.toString(),
                content = ChatUIContent(text = message.content.textContent?.text ?: "")
            )
        }

        fun createTextMessage(
            organizationId: Int,
            conversationId: Long,
            from: Long,
            to: Long,
            message: ChatUIMessage
        ): Message {
            val messageType = message.type
            val content = if (messageType == "text") {
                Content(
                    MessageType.TEXT,
                    textContent = Content.TextContent(message.content.text ?: "")
                )
            } else {
                Content(
                    MessageType.IMAGE,
                    photoContent = Content.PhotoContent("", "", 0, 0)
                )
                TODO("图片消息处理")
            }
            return Message(
                organizationId = organizationId,
                conversationId = conversationId,
                from = from,
                to = to,
                type = CreatorType.STAFF,
                creatorType = CreatorType.CUSTOMER,
                content = content,
            )
        }

        fun createTextMessage(
            organizationId: Int,
            conversationId: Long,
            from: Long,
            to: Long,
            message: String,
            sysCode: SysCode? = null
        ): Message {
            return Message(
                organizationId = organizationId,
                conversationId = conversationId,
                from = from,
                to = to,
                type = CreatorType.STAFF,
                creatorType = CreatorType.CUSTOMER,
                content = Content(
                    MessageType.TEXT,
                    sysCode = sysCode,
                    textContent = Content.TextContent(message)
                )
            )
        }
    }
}
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChatUIContent(
    val text: String? = null,
    val picUrl: String? = null,
    val code: String? = null,
)

data class HistoryResult(
    val lastId: Long,
    val list: List<ChatUIMessage>,
    val total: Int,
)
