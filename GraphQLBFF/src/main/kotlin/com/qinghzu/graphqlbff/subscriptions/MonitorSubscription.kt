package com.qinghzu.graphqlbff.subscriptions

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Subscription
import com.qinghzu.graphqlbff.context.MyGraphQLContext
import com.qinghzu.graphqlbff.model.StaffUnion
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.withAuthentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@GraphQLDescription("订阅客服信息")
@Component
class MonitorSubscription(
    private val staffAdminService: StaffAdminService,
    private val messageService: MessageService,
) : Subscription {

    @GraphQLDescription("查询客服组")
    fun staffOnlineList(@GraphQLIgnore context: MyGraphQLContext, seconds: Long? = 5): Flux<StaffUnion> {
        return Flux.interval(Duration.ofSeconds(seconds ?: 5))
            .flatMap {
                val staffStatusList = messageService.findAllOnlineStaff().withAuthentication(context.oAuth).collectList()
                val staffList = staffAdminService.findAllStaff().withAuthentication(context.oAuth).collectList()
                val staffGroupList = staffAdminService.findAllGroup().withAuthentication(context.oAuth).collectList()
                val staffShuntList = staffAdminService.findAllShunt().withAuthentication(context.oAuth).collectList()
                val customerList = messageService.findAllOnlineCustomer().withAuthentication(context.oAuth).collectList()
                Mono.zip(staffStatusList, staffList, staffGroupList, staffShuntList, customerList)
            }
            .map {
                StaffUnion(
                    it.t1,
                    it.t2,
                    it.t3,
                    it.t4,
                    it.t5,
                )
            }
    }
}