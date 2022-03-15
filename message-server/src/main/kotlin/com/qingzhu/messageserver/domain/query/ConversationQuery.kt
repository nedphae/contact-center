package com.qingzhu.messageserver.domain.query

import com.qingzhu.common.domain.shared.RangeQuery
import com.qingzhu.common.page.PageParam
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder

data class ConversationQuery(
    var organizationId: Int? = null,
    
    val keyword: String?,
    /**
     * 时间
     */
    val timeRange: RangeQuery<String>?,
    /**
     * 责任客服
     */
    val staffIdList: List<Long>?,
    /**
     * 咨询类型
     */
    val categoryList: List<String>?,
    /**
     * 客服组
     */
    val staffGroupIdList: List<Long>?,
    /**
     * 接待组
     */
    val shuntIdList: List<Long>?,
    /**
     * 总消息条数
     */
    val totalMessageCount: RangeQuery<Int>?,
    /**
     * 分页
     */
    val page: PageParam = PageParam(),
    /**
     * 用户 id
     */
    val userId: Long?,
) {
    fun buildSearchQuery(): NativeSearchQueryBuilder {
        var query = NativeSearchQueryBuilder()
        var boolQueryBuilder = BoolQueryBuilder()
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
        if (organizationId != null) {
            boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termsQuery("organizationId", organizationId))
        }
        if (this.timeRange != null) {
            boolQueryBuilder = boolQueryBuilder.must(
                QueryBuilders.rangeQuery("startTime")
                    .from(this.timeRange.from, this.timeRange.includeLower)
                    .to(this.timeRange.to, this.timeRange.includeUpper)
            )
        }
        if (!this.staffIdList.isNullOrEmpty()) {
            boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termsQuery("staffId", *this.staffIdList.toLongArray()))
        }
        if (!this.staffGroupIdList.isNullOrEmpty()) {
            boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termsQuery("fromGroupId", *this.staffGroupIdList.toLongArray()))
        }
        if (!this.shuntIdList.isNullOrEmpty()) {
            boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termsQuery("fromShuntId", *this.shuntIdList.toLongArray()))
        }
        if (userId != null) {
            boolQueryBuilder = boolQueryBuilder.must(QueryBuilders.termsQuery("userId", userId))
        }
        query.withPageable(page.toPageable()).withFilter(boolQueryBuilder)
        return query
    }
}
