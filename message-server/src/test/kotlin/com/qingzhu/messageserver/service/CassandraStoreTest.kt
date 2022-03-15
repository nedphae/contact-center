package com.qingzhu.messageserver.service

import com.qingzhu.common.domain.shared.msg.constant.CreatorType
import com.qingzhu.common.domain.shared.msg.constant.MessageType
import com.qingzhu.common.domain.shared.msg.value.Content
import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.entity.ChatMessageKey
import com.qingzhu.messageserver.domain.entity.ChatMessagePO
import com.qingzhu.messageserver.repository.ChatMessagePORepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.cassandra.core.query.CassandraPageRequest
import reactor.test.StepVerifier
import java.time.Instant
import java.util.*

class CassandraStoreTest : MessageServerApplicationTests() {
    @Autowired
    private lateinit var chatMessagePORepository: ChatMessagePORepository

    @Test
    fun testSaveMessage() {
        val messagePO = ChatMessagePO(
            ChatMessageKey(9491, "1", 1003),
            UUID.randomUUID().toString(),
            Instant.now(),
            100,
            1,
            1,
            CreatorType.CUSTOMER,
            CreatorType.STAFF,
            Content(
                MessageType.TEXT,
                null,
                Content.TextContent("测试时间存储消息"),
            ).toJson(),
            "蜡笔小新",
        )
        val result = chatMessagePORepository.save(messagePO)
            .doOnNext {
                println(it)
            }
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun testGetMessage() {
        val result = chatMessagePORepository.findAllBySeqId(
            9491, "1", 1004,
            CassandraPageRequest.first(2)
        )
            .doOnNext {
                println(it.hasNext())
                println(it.toJson())
            }
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun testGetMessageCount() {
        val result = chatMessagePORepository.countBySeqId(9491, "1", 10001)
            .doOnNext {
                println(it.toJson())
            }
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun testAllCount() {
        val result = chatMessagePORepository.countAll(9491, "1")
            .doOnNext {
                println(it.toJson())
            }
        StepVerifier.create(result)
            .expectNextCount(1)
            .verifyComplete()
    }
}