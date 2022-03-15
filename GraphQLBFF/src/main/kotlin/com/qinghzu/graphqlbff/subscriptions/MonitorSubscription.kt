package com.qinghzu.graphqlbff.subscriptions

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Subscription
import com.qinghzu.graphqlbff.context.MySubscriptionGraphQLContext
import com.qinghzu.graphqlbff.model.MessagePage
import com.qinghzu.graphqlbff.model.StaffUnion
import com.qinghzu.graphqlbff.webclient.MessageService
import com.qinghzu.graphqlbff.webclient.StaffAdminService
import com.qingzhu.common.security.withAuthentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@GraphQLDescription("订阅客服信息")
@Component
class MonitorSubscription(
    private val staffAdminService: StaffAdminService,
    private val messageService: MessageService,
) : Subscription {

    @GraphQLDescription("监控在线客服组")
    fun staffOnlineList(@GraphQLIgnore context: MySubscriptionGraphQLContext, seconds: Long? = 5): Flux<StaffUnion> {
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(seconds ?: 5))
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

    @GraphQLDescription("""同步聊天消息
        |每5秒同步一次，如果是第一次同步，获取最近的20条消息
    """)
    fun monitorMessageByUser(@GraphQLIgnore myGraphQLContext: MySubscriptionGraphQLContext, userId: Long, seconds: Long? = 5): Flux<MessagePage> {
        val atomicLastId = AtomicLong(-1)
        return Flux.interval(Duration.ZERO, Duration.ofSeconds(seconds ?: 5))
            .flatMap {
                val lastId = atomicLastId.get()
                val lastSeqId = if (lastId == -1L) null else lastId
                messageService.syncHistoryMessage(userId, lastSeqId)
                    .doOnNext {
                        atomicLastId.compareAndSet(lastId, it.content?.last()?.seqId ?: -1)
                    }
            }
    }
}