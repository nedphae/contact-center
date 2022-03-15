package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.*
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("查询聊天消息")
@Component
class MessageQuery(private val messageService: MessageService) : Query {
    @GraphQLDescription("根据参数查询历史会话")
    suspend fun searchConv(
        @GraphQLIgnore context: MyGraphQLContext,
        conversationQuery: ConversationQuery
    ): SearchHitPage? {
        return messageService.searchConv(conversationQuery).awaitWithAuthentication(context.oAuth)
    }

    @GraphQLDescription("根据userId拉取历史消息")
    suspend fun loadHistoryMessage(
        @GraphQLIgnore context: MyGraphQLContext,
        userId: Long,
        cursor: Long?,
        limit: Int?
    ): MessagePage? {
        return messageService.loadHistoryMessage(userId, cursor, limit).awaitWithAuthentication(context.oAuth)
    }
}