package com.qingzhu.messageserver.domain.query

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import com.qingzhu.common.page.PageParam
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

data class ConversationQuery(
    val keyword: String?,
    /**
     * 时间
     */
    val timeRange: RangeQuery<String>?,
    /**
     * 责任客服
     */
    @field: JsonSerialize(using = ToStringSerializer::class)
    val staffIdList: List<Long>?,
    /**
     * 咨询类型
     */
    val categoryList: List<String>?,
    /**
     * 客服组
     */
    @field: JsonSerialize(using = ToStringSerializer::class)
    val staffGroupId: List<Long>?,
    /**
     * 总消息条数
     */
    val totalMessageCount: RangeQuery<Int>?,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
) {
    fun buildSearchQuery(): NativeSearchQueryBuilder {
        var query = NativeSearchQueryBuilder()
        if (this.keyword.isNullOrEmpty().not()) {
            val andQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.multiMatchQuery(this.keyword))
                .should(
                    QueryBuilders.nestedQuery(
                        "chatMessages",
                        QueryBuilders.multiMatchQuery(this.keyword),
                        ScoreMode.Avg
                    )
                )
            query = query.withQuery(andQuery)
        }
        if (this.timeRange != null) {
            query = query.withFilter(
                QueryBuilders.rangeQuery("startTime")
                    .from(this.timeRange.from, this.timeRange.includeLower)
                    .to(this.timeRange.to, this.timeRange.includeUpper)
            )
        }
        if (!this.staffIdList.isNullOrEmpty()) {
            query = query.withFilter(QueryBuilders.termsQuery("staffId", *this.staffIdList.toLongArray()))
        }
        if (!this.staffGroupId.isNullOrEmpty()) {
            query = query.withFilter(QueryBuilders.termsQuery("fromGroupId", *this.staffGroupId.toLongArray()))
        }
        query.withPageable(page.toPageable())
        return query
    }
}

data class RangeQuery<T>(
    val from: T? = null,
    val to: T? = null,
    val includeLower: Boolean = true,
    val includeUpper: Boolean = true,
)
