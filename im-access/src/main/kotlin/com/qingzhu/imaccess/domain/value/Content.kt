package com.qingzhu.imaccess.domain.value

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.imaccess.domain.constant.MessageType


@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Content(
        // 内容类型 文字，图片，文件，系统消息
        val contentType: MessageType,
        // 文字
        var textContent: TextContent? = null,
        // 图片
        var photoContent: PhotoContent? = null,
        // 附件
        var attachments: Attachments? = null

) {
    data class TextContent(
            val text: String
    )

    data class PhotoContent(
            // 媒体id
            val mediaId: String,
            // 图片名称
            val filename: String,
            // 图片大小
            val picSize: Int,
            // 图片类型
            val type: Int
    )

    data class Attachments(
            // 媒体id
            val mediaId: String,
            val size: Int,
            // 根据类型显示不同图标
            val type: Int,
            // 文件 路径
            val url: String
    )
}