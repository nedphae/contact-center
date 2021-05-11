package com.qingzhu.dispatcher

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.test.StepVerifier

internal class TestMockWebServer {
    private lateinit var server: MockWebServer
    private lateinit var webClient: WebClient

    @BeforeEach
    fun statMockServer() {
        this.server = MockWebServer()
        this.webClient = WebClient
            .builder()
            .baseUrl(this.server.url("/").toString())
            .build()
    }

    @AfterEach
    fun shutdown() {
        server.shutdown()
    }

    @Test
    fun testServer() {
        this.server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val response = MockResponse()
                    .setHeader("Content-Type", "application/json")
                    .setResponseCode(200)
                return when {
                    request.path.startsWith("/test") -> {
                        response.setBody("good test")
                    }
                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        val result = this.webClient
            .get()
            .uri("/test?id=123")
            .retrieve()
            .bodyToMono<String>()

        StepVerifier.create(result)
            .expectNext("good test")
            .verifyComplete()
    }
}