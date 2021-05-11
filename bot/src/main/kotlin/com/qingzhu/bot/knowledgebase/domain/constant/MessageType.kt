package com.qingzhu.bot.knowledgebase.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

enum class MessageType {
    SYS,
    TEXT,
    IMAGE,
    VOICE,
    FILE,
    LINK
}

/**
 * Reference to the type of creator of the dingtalk design
 *
 * Different from dingtalk's common message/OA message
 *
 * Here distinguish different user types (customer service/customer)
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CreatorType {
    /** system */
    SYS,
    STAFF,
    CUSTOMER,
    GROUP
}