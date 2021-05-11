package com.qingzhu.imaccess.domain.view

import com.fasterxml.jackson.annotation.JsonInclude
import com.qingzhu.common.constant.NoArg
import com.qingzhu.common.message.Header


/**
 * websocket 响应格式
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArg
data class WebSocketResponse<T>(
    /** 消息头 */
    val header: Header,
    /** 返回参数 */
    val code: Int,
    /** 数据 */
    val body: T? = null
) : java.io.Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID = 741L
    }
}

data class WebSocketResponseWithString(
    /** 消息头 */
    val header: Header,
    /** 返回参数 */
    val code: Int,
    /** 数据 */
    val body: String? = null
) : java.io.Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID = 741L
    }
}