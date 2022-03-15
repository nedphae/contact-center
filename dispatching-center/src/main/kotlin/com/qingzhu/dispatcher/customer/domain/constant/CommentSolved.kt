package com.qingzhu.dispatcher.customer.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * 解决状态
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CommentSolved {
    // 未解决
    UNSOLVED,
    // 已解决
    SOLVED,
}
/**
 * 解决方式
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class CommentSolvedWay {
    // 手机
    MOBILE,
    // 邮件
    EMAIL,
}