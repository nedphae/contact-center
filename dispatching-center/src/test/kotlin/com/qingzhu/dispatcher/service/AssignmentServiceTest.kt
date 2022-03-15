package com.qingzhu.dispatcher.service

import com.qingzhu.common.domain.shared.authority.StaffAuthority
import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.DispatcherApplicationTests
import com.qingzhu.dispatcher.component.AssignmentComponent
import com.qingzhu.dispatcher.component.MessageService
import com.qingzhu.dispatcher.component.StaffAdminService
import com.qingzhu.dispatcher.component.impl.WeightedAssignmentService
import com.qingzhu.dispatcher.customer.service.BlacklistService
import com.qingzhu.dispatcher.domain.constant.FromType
import com.qingzhu.dispatcher.domain.dto.ConversationStatusDto
import com.qingzhu.dispatcher.domain.dto.CustomerDispatcherDto
import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import com.qingzhu.dispatcher.domain.dto.StaffDto
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactor.asFlux
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class AssignmentServiceTest : DispatcherApplicationTests() {
    private lateinit var server: MockWebServer

    // Mock redis
    @MockBean
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    @MockBean
    private lateinit var listOps: ReactiveListOperations<String, String>

    @MockBean(name = "innerWebClient")
    private lateinit var webClientBuilder: WebClient.Builder

    private lateinit var assignmentService: AssignmentService

    @MockBean
    private lateinit var messageService: MessageService

    @MockBean
    private lateinit var blacklistService: BlacklistService

    @BeforeEach
    fun startMockServer() {
        // mock redis
        Mockito.`when`(redisTemplate.opsForList()).thenReturn(listOps)
        Mockito.`when`(listOps.leftPush(anyString(), anyString())).thenReturn(Mono.just(1L))
        this.server = MockWebServer()
        val baseUrl = this.server.url("/").toString()
        Mockito.`when`(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder)
        Mockito.`when`(webClientBuilder.build()).thenReturn(WebClient.builder().baseUrl(baseUrl).build())
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    // some mock data
    private val staffList = listOf(
        StaffDispatcherDto(9491, 1, 1L to 15, 30, 10, 1),
        StaffDispatcherDto(9491, 2, 1L to 15, 30, 10, 1)
    )
    final val customerDispatcherDto =
        CustomerDispatcherDto(9491, 1, "test", null, null, 1L, null, null, FromType.WEB, "127.0.0.1", null, null)
    private val botList = listOf(
        StaffDispatcherDto(9491, 3, 1L to 15, 30, 10, 0),
        StaffDispatcherDto(9491, 4, 1L to 15, 30, 10, 0)
    )
    private val staffDto =
        StaffDto(5, 9491, "bot", "bot", StaffAuthority.ROLE_STAFF, 1, "乔巴", "狸猫", null, 0, 50, 1)
    private val conversationStatusDto =
        ConversationStatusDto.fromStaffAndCustomer(staffDto, customerDispatcherDto)

    /**
     * set [MockWebServer] response with [Dispatcher]
     */
    private fun prepareResponse(testBot: Boolean = false) {
        this.server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val response = MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setResponseCode(200)
                return with(request.path) {
                    when {
                        startsWith("/status/staff/idle") -> response.setBody(staffList.toJson())
                        startsWith("/status/staff/assignment") -> response
                        startsWith("/status/customer/shunt-id") -> response.setBody(customerDispatcherDto.toJson())
                        // new conversation
                        startsWith("/status/conversation/find-by-user-id") -> {
                            // if testBot then return empty else return mock bot conversation
                            if (testBot) response else response.setBody(conversationStatusDto.toJson())
                        }
                        startsWith("/status/staff/bot/idle") -> response.setBody(botList.toJson())
                        startsWith("/status/conversation/save") -> response.setBody(request.body)
                        startsWith("/status/conversation/end") -> response
                        startsWith("/staff/info") -> {
                            val staffId = this.last().toString().toLong()
                            val body = StaffDto(
                                staffId,
                                5,
                                "admin",
                                "bot",
                                StaffAuthority.ROLE_STAFF,
                                1,
                                "蒙奇D路飞",
                                "草帽",
                                null,
                                0,
                                50,
                                1
                            )
                            response.setBody(body.toJson())
                        }
                        startsWith("/message/send/assignment") -> response
                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        val assignmentComponent = AssignmentComponent(
            MessageService(webClientBuilder),
            StaffAdminService(webClientBuilder),
            WeightedAssignmentService(),
            redisTemplate
        )
        this.assignmentService = AssignmentService(assignmentComponent, messageService, blacklistService)
    }

    /**
     * test if user visit again in 10 minutes, the status cache has the conversation data
     */
    @Test
    fun assignmentAutoIn10M() {
        prepareResponse()
        val result = (1..10).asFlow()
            .asFlux()
            .flatMap {
                this.assignmentService.assignmentAuto(9491, 1)
            }
            .collectList()
        StepVerifier.create(result)
            .assertNext {
                val idSet = it.sortedBy { dto -> dto.staffId }.map { dto -> dto.staffId }.toSet()
                println(idSet.toJson())
                assertIterableEquals(listOf(5L), idSet)
            }
            .verifyComplete()
    }

    @Test
    fun assignmentAuto() {
        prepareResponse(true)
        val result = (1..10).asFlow()
            .asFlux()
            .flatMap {
                this.assignmentService.assignmentAuto(9491, 1)
            }
            .collectList()

        StepVerifier.create(result)
            .assertNext {
                val idSet = it.sortedBy { dto -> dto.staffId }.map { dto -> dto.staffId }.toSet()
                println(idSet.toJson())
                assertIterableEquals(listOf(3L, 4L), idSet)
            }
            .verifyComplete()
    }

    @Test
    fun assignmentStaff() {
        prepareResponse()
        val result = (1..10).asFlow()
            .asFlux()
            .flatMap {
                this.assignmentService.assignmentStaff(9491, 1)
            }
            .collectList()

        StepVerifier.create(result)
            .assertNext {
                val idSet = it.sortedBy { dto -> dto.staffId }.map { dto -> dto.staffId }.toSet()
                println(idSet.toJson())
                assertIterableEquals(listOf(1L, 2L), idSet)
            }
            .verifyComplete()
    }
}