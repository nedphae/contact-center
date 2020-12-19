package com.qingzhu.imaccess.util

/**
 * ip 转换为 Long
 * ip 开始段大于 128 后会溢出为负数
 */
fun convertIP2Long(ip: String): Long {
    val parts = ip.split(".").map { it.toInt() }
    var result = 0L
    (0..3).forEach {
        result = result shl 8 or parts[it].toLong()
    }
    return result
}