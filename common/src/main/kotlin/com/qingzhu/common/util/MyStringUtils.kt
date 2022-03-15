package com.qingzhu.common.util

import java.net.URLEncoder

fun String.isDate(): Boolean {
    return this.matches(Regex("[\\d]{4}-[\\d]{2}-[\\d]{2} [\\d]{2}:[\\d]{2}:[\\d]{2}"))
}

fun String.mySubstring(): String {
    val param = URLEncoder.encode(this, "UTF-8")
    return if (this.length > 45) param.substring(0, 45) else param
}