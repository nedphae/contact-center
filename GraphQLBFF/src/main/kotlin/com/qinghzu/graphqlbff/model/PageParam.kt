package com.qinghzu.graphqlbff.model

import com.qingzhu.common.constant.NoArg

/**
 * 分页参数
 */
@NoArg
data class PageParam(
    val page: Int = 0,    //当前查询页码
    val size: Int = 20,   //每页显示条数
    val direction: Direction = Direction.DESC,  //排序规则
    var properties: List<String>? = null,
)

enum class Direction {
    ASC, DESC;
}

