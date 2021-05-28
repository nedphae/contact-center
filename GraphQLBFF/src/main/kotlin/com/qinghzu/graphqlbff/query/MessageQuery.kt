package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.ConversationQuery
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class MessageQuery(private val messageService: MessageService) {
    @GraphQLDescription("根据参数查询历史消息")
    suspend fun searchConv(@GraphQLIgnore context: MyGraphQLContext, conversationQuery: ConversationQuery): String? {
        return messageService.searchConv(conversationQuery).awaitWithAuthentication(context.oAuth)
    }
}