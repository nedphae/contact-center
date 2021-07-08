package com.qinghzu.graphqlbff.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.BotConfig
import com.qinghzu.graphqlbff.model.KnowledgeBase
import com.qinghzu.graphqlbff.model.Topic
import com.qinghzu.graphqlbff.model.TopicCategory
import com.qinghzu.graphqlbff.webclient.BotService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@GraphQLDescription("机器人信息查询")
@Component
class BotQuery(private val botService: BotService): Query {
    suspend fun allTopic(@GraphQLIgnore context: MyGraphQLContext): List<Topic> {
        return botService.findAllTopic().awaitWithAuthentication(context.oAuth)
    }
    suspend fun allBotConfig(@GraphQLIgnore context: MyGraphQLContext): List<BotConfig> {
        return botService.findAllBotConfig().awaitWithAuthentication(context.oAuth)
    }
    suspend fun allKnowledgeBase(@GraphQLIgnore context: MyGraphQLContext): List<KnowledgeBase> {
        return botService.findAllKnowledgeBase().awaitWithAuthentication(context.oAuth)
    }
    suspend fun allTopicCategory(@GraphQLIgnore context: MyGraphQLContext): List<TopicCategory> {
        return botService.findAllTopicCategory().awaitWithAuthentication(context.oAuth)
    }
}