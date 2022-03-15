package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.model.QuickReplyAllDto
import org.springframework.stereotype.Component

@Component
class QuickReplyQuery : Query {
    @GraphQLDescription("获取话术")
    suspend fun getQuickReply(): QuickReplyAllDto {
        return QuickReplyAllDto()
    }
}