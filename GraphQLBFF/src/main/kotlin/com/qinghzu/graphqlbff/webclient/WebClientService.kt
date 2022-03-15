package com.qinghzu.graphqlbff.webclient

import com.qinghzu.graphqlbff.model.*
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

/**
 * NOTE: 因为 这个是通过 kotlin coroutines 调用的，没有通过 spring reactive，所以无法通过request获取 jwt token
 * 完成: innerWebClient 仅仅测试使用，后期修改为 bearerWebClient (使用 MyGraphQLContextFactory)
 */
@Component
class MessageService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://message-server") {

    fun updateStaffStatus(updateStaffStatus: UpdateStaffStatus): Mono<StaffStatus> {
        return webClient
            .put()
            .uri("/status/register/staff")
            .bodyValue(updateStaffStatus)
            .retrieve()
            .bodyToMono()
    }

    fun findCustomerStatus(userId: Long): Mono<CustomerStatus> {
        return webClient
            .get()
            .uri {
                it.path("/status/customer/find-by-id")
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun searchConv(conversationQuery: ConversationQuery): Mono<SearchHitPage> {
        return webClient
            .post()
            .uri("/message/q")
            .bodyValue(conversationQuery)
            .retrieve()
            .bodyToMono()
    }

    fun findAllOnlineStaff(): Flux<StaffStatus> {
        return webClient
            .get()
            .uri("/status/staff/online")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllOnlineCustomer(): Flux<CustomerStatus> {
        return webClient
            .get()
            .uri("/status/customer/online")
            .retrieve()
            .bodyToFlux()
    }

    fun syncHistoryMessage(userId: Long, lastSeqId: Long?): Mono<MessagePage> {
        return webClient
            .get()
            .uri {
                it.path("/message/history/customer/sync")
                    .queryParam("userId", userId)
                    .queryParam("lastSeqId", lastSeqId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun loadHistoryMessage(
        userId: Long,
        lastSeqId: Long?,
        pageSize: Int?
    ): Mono<MessagePage> {
        return webClient
            .get()
            .uri {
                it.path("/message/history")
                    .queryParam("userId", userId)
                    .queryParam("lastSeqId", lastSeqId)
                    .queryParam("pageSize", pageSize)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

}

@Component
class CustomerService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) :
    BaseWebClient(webClientBuilder, "http://dispatching-center") {

    fun searchCustomer(customerQuery: CustomerQuery): Mono<CustomerSearchHitPage> {
        return webClient
            .post()
            .uri("/customer/q")
            .bodyValue(customerQuery)
            .retrieve()
            .bodyToMono()
    }

    fun findCustomer(userId: Long): Mono<Customer> {
        return webClient
            .get()
            .uri {
                it.path("/customer")
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun findConversationByUserId(userId: Long): Mono<Conversation> {
        return webClient
            .get()
            .uri {
                it.path("/status/conversation/find-by-user-id")
                    .queryParam("userId", userId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun updateCustomer(customerDto: Mono<Customer>): Mono<Customer> {
        return webClient
            .put()
            .uri("/customer")
            .body(customerDto)
            .retrieve()
            .bodyToMono()
    }

    fun deleteCustomerByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/customer/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun assignmentFromQueueForStaff(staffStatus: StaffStatus): Mono<ResponseEntity<Void>> {
        return webClient
            .put()
            .uri("/dispatcher/assignment/queue/for-staff")
            .bodyValue(staffStatus)
            .retrieve()
            .toBodilessEntity()
    }

    fun findComment(commentQuery: CommentQuery): Mono<CommentPage> {
        return webClient
            .post()
            .uri("/customer/comment/q")
            .bodyValue(commentQuery)
            .retrieve()
            .bodyToMono()
    }

    fun saveComment(comment: List<Comment>): Flux<Comment> {
        return webClient
            .post()
            .uri("/customer/comment")
            .body(comment.toFlux())
            .retrieve()
            .bodyToFlux()
    }

    fun getAllBlacklist(audited: Boolean?): Flux<Blacklist> {
        return webClient
            .get()
            .uri {
                it.path("/customer/blacklist/all")
                    .queryParam("audited", audited)
                    .build()
            }
            .retrieve()
            .bodyToFlux()
    }

    fun getBlacklistBy(preventStrategy: String, preventSource: String): Mono<Blacklist> {
        return webClient
            .get()
            .uri {
                it.path("/customer/blacklist")
                    .queryParam("preventStrategy", preventStrategy)
                    .queryParam("preventSource", preventSource)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun saveBlacklist(blacklist: List<BlacklistInput>): Flux<Blacklist> {
        return webClient
            .post()
            .uri("/customer/blacklist")
            .body(blacklist.toFlux())
            .retrieve()
            .bodyToFlux()
    }

    fun deleteBlacklist(blacklist: List<BlacklistInput>): Mono<Long> {
        return webClient
            .post()
            .uri("/customer/blacklist/delete")
            .body(blacklist.toFlux())
            .retrieve()
            .bodyToMono()
    }
}

@Service
class StaffAdminService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://staff-admin").build()

    fun findStaffById(staffId: Long): Mono<Staff> {
        return webClient
            .get()
            .uri {
                it.path("/staff/info")
                    .queryParam("staffId", staffId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun findQuickReplyByStaff(): Mono<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/personal")
            .retrieve()
            .bodyToMono()
    }

    fun findQuickReplyByOrganizationId(): Mono<QuickReplyDto> {
        return webClient
            .get()
            .uri("/staff/quick-reply/all")
            .retrieve()
            .bodyToMono()
    }

    fun findAllGroup(): Flux<StaffGroup> {
        return webClient
            .get()
            .uri("/staff/group/all")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllShunt(): Flux<Shunt> {
        return webClient
            .get()
            .uri("/staff/shunt")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllStaff(): Flux<Staff> {
        return webClient
            .get()
            .uri("/staff/all")
            .retrieve()
            .bodyToFlux()
    }

    fun findStaffConfigByShuntId(shuntId: Long): Flux<StaffConfig> {
        return webClient
            .get()
            .uri {
                it.path("/staff/config/${shuntId}")
                    .build()
            }
            .retrieve()
            .bodyToFlux()
    }

    fun saveQuickReply(staffConfigList: List<StaffConfig>): Flux<StaffConfig> {
        return webClient
            .post()
            .uri("/staff/config")
            .body(staffConfigList.toFlux())
            .retrieve()
            .bodyToFlux()
    }

    fun saveQuickReply(quickReply: QuickReplyInput): Mono<QuickReply> {
        return webClient
            .post()
            .uri("/staff/quick-reply")
            .bodyValue(quickReply)
            .retrieve()
            .bodyToMono()
    }

    fun saveQuickReplyGroup(quickReplyGroup: QuickReplyGroupInput): Mono<QuickReplyGroup> {
        return webClient
            .post()
            .uri("/staff/quick-reply/group")
            .bodyValue(quickReplyGroup)
            .retrieve()
            .bodyToMono()
    }

    fun deleteQuickReply(id: Long): Mono<Void> {
        return webClient
            .delete()
            .uri("/staff/quick-reply/${id}")
            .retrieve()
            .bodyToMono()
    }

    fun deleteQuickReplyGroup(id: Long): Mono<Void> {
        return webClient
            .delete()
            .uri("/staff/quick-reply/group/${id}")
            .retrieve()
            .bodyToMono()
    }

    fun saveStaff(staff: Staff): Mono<Staff> {
        return webClient
            .post()
            .uri("/staff")
            .bodyValue(staff)
            .retrieve()
            .bodyToMono()
    }

    fun deleteStaffByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/staff/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun deleteShuntByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/staff/shunt/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun deleteGroupByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/staff/group/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun saveStaffGroup(staffGroup: StaffGroup): Mono<StaffGroup> {
        return webClient
            .post()
            .uri("/staff/group")
            .bodyValue(staffGroup)
            .retrieve()
            .bodyToMono()
    }

    fun saveShunt(shunt: Shunt): Mono<Shunt> {
        return webClient
            .post()
            .uri("/staff/shunt")
            .bodyValue(shunt)
            .retrieve()
            .bodyToMono()
    }

    fun findAllShuntClass(): Flux<ShuntClass> {
        return webClient
            .get()
            .uri("/staff/shunt/class/all")
            .retrieve()
            .bodyToFlux()
    }

    fun saveShuntClass(shuntClass: ShuntClass): Mono<ShuntClass> {
        return webClient
            .post()
            .uri("/staff/shunt/class")
            .bodyValue(shuntClass)
            .retrieve()
            .bodyToMono()
    }

    fun getUIConfigByShunt(shuntId: Long): Mono<ShuntUIConfig> {
        return webClient
            .get()
            .uri {
                it.path("/config/chat-ui/config")
                    .queryParam("shuntId", shuntId)
                    .build()
            }
            .retrieve()
            .bodyToMono()
    }

    fun saveUIConfig(shuntUIConfig: ShuntUIConfig): Mono<ShuntUIConfig> {
        return webClient
            .post()
            .uri("/config/chat-ui/config")
            .bodyValue(shuntUIConfig)
            .retrieve()
            .bodyToMono()
    }

    fun getAllProperties(): Mono<String> {
        return webClient
            .get()
            .uri("/config/props")
            .retrieve()
            .bodyToMono()
    }

    fun updateProperties(properties: List<Properties>): Flux<Properties> {
        return webClient
            .put()
            .uri("/config/props")
            .bodyValue(properties)
            .retrieve()
            .bodyToFlux()
    }
}

@Service
class BotService(@Qualifier("bearerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://bot").build()

    fun findAllTopic(): Flux<Topic> {
        return webClient
            .get()
            .uri("/bot/manage/topic")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllBotConfig(): Flux<BotConfig> {
        return webClient
            .get()
            .uri("/bot/manage/botConfig")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllKnowledgeBase(): Flux<KnowledgeBase> {
        return webClient
            .get()
            .uri("/bot/manage/knowledgeBase")
            .retrieve()
            .bodyToFlux()
    }

    fun findAllTopicCategory(): Flux<TopicCategory> {
        return webClient
            .get()
            .uri("/bot/manage/topicCategory")
            .retrieve()
            .bodyToFlux()
    }

    fun saveTopic(topic: Topic): Mono<Topic> {
        return webClient
            .post()
            .uri("/bot/manage/topic")
            .bodyValue(topic)
            .retrieve()
            .bodyToMono()
    }

    fun saveBotConfig(botConfig: BotConfig): Mono<BotConfig> {
        return webClient
            .post()
            .uri("/bot/manage/botConfig")
            .bodyValue(botConfig)
            .retrieve()
            .bodyToMono()
    }

    fun saveKnowledgeBase(knowledgeBase: KnowledgeBase): Mono<KnowledgeBase> {
        return webClient
            .post()
            .uri("/bot/manage/knowledgeBase")
            .bodyValue(knowledgeBase)
            .retrieve()
            .bodyToMono()
    }

    fun saveTopicCategory(topicCategory: TopicCategory): Mono<TopicCategory> {
        return webClient
            .post()
            .uri("/bot/manage/topicCategory")
            .bodyValue(topicCategory)
            .retrieve()
            .bodyToMono()
    }

    fun deleteTopicByIds(ids: List<String>): Mono<Void> {
        return webClient
            .put()
            .uri("/bot/manage/topic/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun deleteBotConfigByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/bot/manage/botConfig/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun deleteKnowledgeBaseByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/bot/manage/knowledgeBase/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }

    fun deleteTopicCategoryByIds(ids: List<Long>): Mono<Void> {
        return webClient
            .put()
            .uri("/bot/manage/topicCategory/deleteByIds")
            .body(ids.toFlux())
            .retrieve()
            .bodyToMono()
    }
}