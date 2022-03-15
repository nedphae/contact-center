package com.qingzhu.imaccess.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.domain.shared.msg.value.Message
import com.qingzhu.imaccess.ImApplicationTests
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.core.publisher.Mono
import java.time.Instant

internal class MessageFilterServiceTest : ImApplicationTests() {

    @Autowired
    private lateinit var messageFilterService: MessageFilterService

    @Test
    fun testFilter() {
        Mono.just(
            Message(
                9491,
                "1",
                2,
                Instant.now(),
                1,
                1,
                1,
                CreatorType.CUSTOMER,
                CreatorType.STAFF,
                Content(
                    MessageType.TEXT,
                    textContent = Content.TextContent("test")
                )
            )
        )
            .transform(messageFilterService::filter)
            .doOnDiscard(Message::class.java) {
                println("过滤：$it")
            }
            .subscribe()
    }
}