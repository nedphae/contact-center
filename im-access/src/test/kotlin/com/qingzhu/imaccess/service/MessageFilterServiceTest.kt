package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.ImApplicationTests
import com.qingzhu.imaccess.domain.constant.CreatorType
import com.qingzhu.imaccess.domain.constant.MessageType
import com.qingzhu.imaccess.domain.value.Content
import com.qingzhu.imaccess.domain.value.Message
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono

internal class MessageFilterServiceTest : ImApplicationTests() {

    @Autowired
    private lateinit var messageFilterService: MessageFilterService

    @Test
    fun testFilter() {
        Mono.just(Message(
                "1",
                2,
                null,
                1,
                CreatorType.CUSTOMER,
                CreatorType.STAFF,
                Content(
                        MessageType.TEXT,
                        Content.TextContent("test")
                )
        ))
                .transform(messageFilterService::filter)
                .doOnDiscard(Message::class.java) {
                    println("过滤：$it")
                }
                .subscribe()
    }
}