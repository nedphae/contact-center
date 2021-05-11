package com.qinghzu.graphqlbff

import com.qinghzu.graphqlbff.exceptions.CustomDataFetcherExceptionHandler
import com.qinghzu.graphqlbff.execution.CustomDataFetcherFactoryProvider
import com.qinghzu.graphqlbff.execution.MySubscriptionHooks
import com.qinghzu.graphqlbff.execution.SpringDataFetcherFactory
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.server.spring.subscriptions.ApolloSubscriptionHooks
import com.fasterxml.jackson.databind.ObjectMapper
import com.qinghzu.graphqlbff.directives.CustomDirectiveWiringFactory
import com.qinghzu.graphqlbff.hooks.CustomSchemaGeneratorHooks
import graphql.execution.DataFetcherExceptionHandler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean

@SpringBootApplication
class GraphQlbffApplication {
    @Bean
    fun wiringFactory() = CustomDirectiveWiringFactory()

    @Bean
    fun hooks(wiringFactory: KotlinDirectiveWiringFactory) = CustomSchemaGeneratorHooks(wiringFactory)

    @Bean
    fun dataFetcherFactoryProvider(
        springDataFetcherFactory: SpringDataFetcherFactory,
        objectMapper: ObjectMapper,
        applicationContext: ApplicationContext
    ) = CustomDataFetcherFactoryProvider(springDataFetcherFactory, objectMapper, applicationContext)

    @Bean
    fun dataFetcherExceptionHandler(): DataFetcherExceptionHandler = CustomDataFetcherExceptionHandler()

    @Bean
    fun apolloSubscriptionHooks(): ApolloSubscriptionHooks = MySubscriptionHooks()
}

fun main(args: Array<String>) {
    runApplication<GraphQlbffApplication>(*args)
}
