package com.qingzhu.imaccess.domain.constant

import com.fasterxml.jackson.annotation.JsonFormat

/**
 * 来源类型
 * web/ios/android/wx(微信)/wx_ma(微信小程序)/wb(微博)/open(开放接口)
 */
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
enum class FromType(val type: String) {
    WEB("web"),
    IOS("ios"),
    ANDROID("android"),
    WX("微信"),
    WX_MA("微信小程序"),
    WB("微博"),
    OPEN("开放接口"),
}