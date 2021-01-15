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
        var properties: Array<out String> = arrayOf("id")
) {
    fun toPageable(): Pageable {
        return PageRequest.of(page, size, direction, *properties)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PageParam

        if (page != other.page) return false
        if (size != other.size) return false
        if (direction != other.direction) return false
        if (!properties.contentEquals(other.properties)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = page
        result = 31 * result + this.size
        result = 31 * result + direction.hashCode()
        result = 31 * result + properties.contentHashCode()
        return result
    }

    companion object {
        fun defaultPage(vararg properties: String): PageParam {
            return PageParam(0, 20, Sort.Direction.DESC, properties)
        }
    }
}

@NoArg
data class PageParamWithoutSort(
        val page: Int = 0,
        val size: Int = 20
) {
    fun toPageable(): Pageable {
        return PageRequest.of(page, size)
    }

    fun toPageParam(direction: Sort.Direction, vararg properties: String): PageParam {
        return PageParam(page, size, direction, properties)
    }
}