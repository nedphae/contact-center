package com.qingzhu.common.page

import com.qingzhu.common.constant.NoArg
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 分页参数
 */
@NoArg
data class PageParam(
    val page: Int = 0,    //当前查询页码
    val size: Int = 20,   //每页显示条数
    val direction: Sort.Direction = Sort.Direction.DESC,  //排序规则
    var properties: List<String>? = null,
) {
    fun toPageable(): Pageable {
        return this.properties?.let { PageRequest.of(this.page, this.size, this.direction, *it.toTypedArray()) }
            ?: PageRequest.of(this.page, this.size)
    }
}