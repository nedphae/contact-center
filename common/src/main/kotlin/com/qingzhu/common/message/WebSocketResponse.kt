package com.qingzhu.common.message

import com.qingzhu.common.constant.NoArg


interface WebSocketResponse<T> : java.io.Serializable

@NoArg
data class WebSocketResponseWithBody<T>(
        // 消息头
        val header: Header,
        val code: Int,
        val body: T? = null
) : WebSocketResponse<T> {
    companion object {
        @JvmStatic
        private val serialVersionUID = 741
    }
}

@NoArg
data class WebSocketResponseWithoutBody(
        // 消息头
        val header: Header,
        val code: Int
) : WebSocketResponse<Unit> {
    companion object {
        @JvmStatic
        private val serialVersionUID = 741
    }
}