package com.qingzhu.common.domain.shared.msg.value

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.constant.SysCode

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Content(
    /** Content type:
     *
     * text, picture, file, system message(text)
     */
    val contentType: MessageType,
    /** sys 消息类型 **/
    val sysCode: SysCode? = null,
    /** text */
    var textContent: TextContent? = null,
    /** picture */
    var photoContent: PhotoContent? = null,
    /** file */
    var attachments: Attachments? = null

) {
    data class TextContent(
        val text: String
    )

    data class PhotoContent(
        val mediaId: String,
        val filename: String,
        val picSize: Int,
        /** pic type */
        val type: String
    )

    data class Attachments(
        val mediaId: String,
        val filename: String,
        val size: Int,
        /** Display different icons according to the type */
        val type: String,
    )
}