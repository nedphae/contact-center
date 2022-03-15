package com.qingzhu.common.domain.shared

data class RangeQuery<T>(
    val from: T? = null,
    val to: T? = null,
    val includeLower: Boolean = true,
    val includeUpper: Boolean = true,
)