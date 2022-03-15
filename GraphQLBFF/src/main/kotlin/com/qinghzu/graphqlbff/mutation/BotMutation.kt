package com.qinghzu.graphqlbff.mutation

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Mutation
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.BotConfig
import com.qinghzu.graphqlbff.model.KnowledgeBase
import com.qinghzu.graphqlbff.model.Topic
import com.qinghzu.graphqlbff.model.TopicCategory
import com.qinghzu.graphqlbff.webclient.BotService
import com.qingzhu.common.security.awaitWithAuthentication
import org.springframework.stereotype.Component

@Component
class BotMutation(private val botService: BotService): Mutation {
    suspend fun saveTopic(@GraphQLIgnore context: MyGraphQLContext, topic: Topic): Topic? {
        return botService.saveTopic(topic).awaitWithAuthentication(context.oAuth)
    }

    suspend fun saveBotConfig(@GraphQLIgnore context: MyGraphQLContext, botConfig: BotConfig): BotConfig? {
        return botService.saveBotConfig(botConfig).awaitWithAuthentication(context.oAuth)
    }

    suspend fun saveKnowledgeBase(@GraphQLIgnore context: MyGraphQLContext, knowledgeBase: KnowledgeBase): KnowledgeBase? {
        return botService.saveKnowledgeBase(knowledgeBase).awaitWithAuthentication(context.oAuth)
    }

    suspend fun saveTopicCategory(@GraphQLIgnore context: MyGraphQLContext, topicCategory: TopicCategory): TopicCategory? {
        return botService.saveTopicCategory(topicCategory).awaitWithAuthentication(context.oAuth)
    }

    suspend fun deleteTopicByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<String>): List<String> {
        botService.deleteTopicByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }

    suspend fun deleteBotConfigByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long> {
        botService.deleteBotConfigByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }

    suspend fun deleteKnowledgeBaseByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long> {
        botService.deleteKnowledgeBaseByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }

    suspend fun deleteTopicCategoryByIds(@GraphQLIgnore context: MyGraphQLContext, ids: List<Long>): List<Long> {
        botService.deleteTopicCategoryByIds(ids).awaitWithAuthentication(context.oAuth)
        return ids
    }
}