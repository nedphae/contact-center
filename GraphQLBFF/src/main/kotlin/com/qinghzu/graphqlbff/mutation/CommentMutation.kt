package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.Comment
import com.qinghzu.graphqlbff.webclient.CustomerService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class CommentMutation(private val customerService: CustomerService): Mutation {
    @GraphQLDescription("修改客户留言信息")
    suspend fun saveComment(@GraphQLIgnore context: MyGraphQLContext, @GraphQLDescription("修改的客户基本信息") commentList: List<Comment>): List<Comment>? {
        return customerService.saveComment(commentList).awaitWithAuthentication(context.oAuth)
    }
}