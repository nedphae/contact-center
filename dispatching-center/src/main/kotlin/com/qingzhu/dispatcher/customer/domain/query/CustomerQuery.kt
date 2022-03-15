package com.qingzhu.dispatcher.customer.domain.query

import com.qingzhu.common.domain.shared.RangeQuery
import com.qingzhu.common.page.PageParam
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

data class CustomerQuery(
    var organizationId: Int? = null,

    val keyword: String?,
    /**
     * 时间
     */
    val timeRange: RangeQuery<String>?,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
) {
    fun buildSearchQuery(): NativeSearchQueryBuilder {
        var query = NativeSearchQueryBuilder()
        var boolQueryBuilder = BoolQueryBuilder()
        if (this.keyword.isNullOrEmpty().not()) {
            val andQuery = QueryBuilders.multiMatchQuery(this.keyword)
            query = query.withQuery(andQuery)
        }
        if (this.timeRange != null) {
            boolQueryBuilder = boolQueryBuilder.must(
                QueryBuilders.rangeQuery("createdDate")
                    .from(this.timeRange.from, this.timeRange.includeLower)
                    .to(this.timeRange.to, this.timeRange.includeUpper)
            )
        }
        query.withPageable(page.toPageable()).withFilter(boolQueryBuilder)
        return query
    }
}
