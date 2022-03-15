package com.qingzhu.common.domain.shared.msg.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.JsonNode
import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType.*
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
    val context: JsonNode? = null,
) {
    companion object {
        fun fromMessage(message: Message) : ChatUIMessage {
            val position = if (message.creatorType == CreatorType.CUSTOMER) "right" else "left"
            return when(message.content.contentType) {
                TEXT -> ChatUIMessage(
                    _id = message.seqId.toString(),
                    content = ChatUIContent(text = message.content.textContent?.text ?: ""),
                    position = position,
                )
                SYS -> TODO()
                IMAGE -> ChatUIMessage(
                    _id = message.seqId.toString(),
                    type = "image",
                    content = ChatUIContent(picUrl = message.content.photoContent?.mediaId ?: ""),
                    position = position,
                )
                VOICE -> TODO()
                FILE -> TODO()
                LINK -> TODO()
            }
        }

        fun createMessage(
            organizationId: Int,
            conversationId: Long,
            from: Long,
            to: Long,
            message: ChatUIMessage,
            type: CreatorType = CreatorType.STAFF,
            creatorType: CreatorType =  CreatorType.CUSTOMER,
            sysCode: SysCode? = null,
            nickName: String? = null,
        ): Message {
            val messageType = message.type
            val content = if (messageType == "text") {
                Content(
                    TEXT,
                    sysCode,
                    textContent = Content.TextContent(message.content.text ?: "")
                )
            } else {
                Content(
                    IMAGE,
                    sysCode,
                    photoContent = Content.PhotoContent("", "", 0, "png")
                )
                TODO("图片消息处理")
            }
            return Message(
                organizationId = organizationId,
                conversationId = conversationId,
                from = from,
                to = to,
                nickName = nickName,
                type = type,
                creatorType = creatorType,
                content = content
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CardData(
    val title: String? = null,
    val url: String? = null,
    val text: String? = null,
    val hot: Boolean? = null,
)
// 快捷短语
@JsonInclude(JsonInclude.Include.NON_NULL)
data class QuickReplies(
    val name: String? = null,
    val text: String? = null,
    val type: String? = null,
    val url: String? = null,
    val card: ChatUIContent? = null,
)
// 卡片消息
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Card(
    val hideShortcuts: Boolean? = null,
    // 点选卡片slot, 推荐列表卡片recommend
    val list: List<CardData>? = null,
    // knowledge 内容（支持富文本）
    val text: String? = null,
)

// 基本消息
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ChatUIContent(
    // code: promotion,knowledge,slot,recommend,quick-replies,
    val code: String? = null,
    val text: String? = null,
    val picUrl: String? = null,
    val data: Card? = null,
    val list: List<QuickReplies>? = null,
)

data class HistoryResult(
    val lastId: Long?,
    val list: List<ChatUIMessage>,
    val hasNext: Boolean,
)
