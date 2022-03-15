package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.CommentPage
import com.qinghzu.graphqlbff.model.CommentQuery
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class CommentQuery(private val customerService: CustomerService) : Query {
    @GraphQLDescription("分页查询客户留言信息")
    suspend fun findComment(
        @GraphQLIgnore context: MyGraphQLContext,
        commentQuery: CommentQuery
    ): CommentPage? {
        return customerService.findComment(commentQuery).awaitWithAuthentication(context.oAuth)
    }
}
