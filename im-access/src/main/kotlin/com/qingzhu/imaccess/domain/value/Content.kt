package com.qingzhu.imaccess.domain.value

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.imaccess.domain.constant.MessageType

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class Content(
        /** Content type:
         *
         * text, picture, file, system message(text)
         */
        val contentType: MessageType,
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
            val type: Int
    )

    data class Attachments(
            val mediaId: String,
            val size: Int,
            /** Display different icons according to the type */
            val type: Int,
            /** file path */
            val url: String
    )
}