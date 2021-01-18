package com.qingzhu.dispatcher.service

import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.DispatcherApplicationTests
import com.qingzhu.dispatcher.component.AssignmentComponent
import com.qingzhu.dispatcher.component.MessageService
import com.qingzhu.dispatcher.component.StaffAdminService
import com.qingzhu.dispatcher.component.impl.WeightedAssignmentService
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
import org.junit.jupiter.api.Assertions.assertTrue
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
    private lateinit var webClientBuilder: WebClient.Builder
    private lateinit var baseUrl: String

    // Mock redis
    @MockBean
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    @MockBean
    private lateinit var listOps: ReactiveListOperations<String, String>

    private lateinit var assignmentService: AssignmentService

    @BeforeEach
    fun statMockServer() {
        // mock redis
        Mockito.`when`(redisTemplate.opsForList()).thenReturn(listOps)
        Mockito.`when`(listOps.leftPush(anyString(), anyString())).thenReturn(Mono.just(1L))

        this.server = MockWebServer()
        this.baseUrl = this.server.url("/").toString()
        this.webClientBuilder = WebClient.builder()
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    // some mock data
    private final val staffList = listOf(
            StaffDispatcherDto(9491, 1, 1L to 15, 30, 10, 1),
            StaffDispatcherDto(9491, 2, 1L to 15, 30, 10, 1)
    )
    final val customerDispatcherDto = CustomerDispatcherDto(9491, 1, null, null, 1L, null, null, FromType.WEB, "127.0.0.1", null, null)
    private final val botList = listOf(
            StaffDispatcherDto(9491, 3, 1L to 15, 30, 10, 0),
            StaffDispatcherDto(9491, 4, 1L to 15, 30, 10, 0)
    )
    private final val staffDto = StaffDto(9491, 3, 1, "bot", "乔巴", "狸猫", 0, "", 1)
    private final val conversationStatusDto = ConversationStatusDto.fromStaffAndCustomer(staffDto, customerDispatcherDto)

    /**
     * set [MockWebServer] response with [Dispatcher]
     */
    fun prepareResponse(testBot: Boolean = false) {
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
                            val body = StaffDto(9491, staffId, 1, "admin", "蒙奇D路飞", "草帽", 0, "", 1)
                            response.setBody(body.toJson())
                        }
                        else -> MockResponse().setResponseCode(404)
                    }
                }
            }
        }

        val assignmentComponent = AssignmentComponent(
                MessageService(webClientBuilder, baseUrl),
                StaffAdminService(webClientBuilder, baseUrl),
                WeightedAssignmentService(),
                redisTemplate
        )
        this.assignmentService = AssignmentService(assignmentComponent)
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
                    println(it.toJson())
                    assertTrue(it.map { dto -> dto.staffId }.containsAll(listOf(3L, 4L)))
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
                    println(it.toJson())
                    assertTrue(it.map { dto -> dto.staffId }.containsAll(listOf(1L, 2L)))
                }
                .verifyComplete()
    }
}