package com.qingzhu.imaccess.domain.query

import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.common.message.Header

class WebSocketRequestStaffConfig(
    header: Header,
    /** 消息体 */
    body: StaffConfig
) : WebSocketRequest<StaffConfig>(header, body)

class WebSocketRequestCustomerConfig(
    header: Header,
    /** 消息体 */
    body: AssignmentInfo
) : WebSocketRequest<AssignmentInfo>(header, body)

class WebSocketRequestMessage(
    header: Header,
    /** 消息体 */
    body: Message
) : WebSocketRequest<Message>(header, body)