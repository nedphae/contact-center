package com.qingzhu.imaccess.domain.query

import com.qingzhu.common.message.Header

class WebSocketRequestStaffConfig(
        header: Header,
        /** 消息体 */
        body: StaffConfig
) : WebSocketRequest<StaffConfig>(header, body)