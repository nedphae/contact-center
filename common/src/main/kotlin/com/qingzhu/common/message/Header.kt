package com.qingzhu.common.message

import com.qingzhu.common.constant.NoArg

@NoArg
data class Header(
    /**
     * 消息id
     * 客户端生成消息id
     * 用于验证服务器消息回复
     */
    var mid: String,
    var sid: String?
) : java.io.Serializable {
    companion object {
        @JvmStatic
        private val serialVersionUID = 741
    }
}