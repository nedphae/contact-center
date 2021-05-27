package com.qingzhu.messageserver.service

import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.entity.Conversation
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.test.StepVerifier

internal class MessagePersistentServiceTest : MessageServerApplicationTests() {

    @Autowired
    private lateinit var reactiveElasticsearchTemplate: ReactiveElasticsearchTemplate

    @Test
    fun testIndex() {
        // ik 分词器原因无法建立 mapping
        val ops = reactiveElasticsearchTemplate.indexOps(Conversation::class.java)
        val mapping = ops.createMapping(Conversation::class.java)
        val test = mapping
            .doOnNext {
                println(it.toJson())
            }
            .transform {
                ops.putMapping(it)
            }
            .switchIfEmpty { Mono.just(false) }
            .flatMap {
                println(it)
                ops.refresh()
            }
            .doOnError {
                it.printStackTrace()
            }
        StepVerifier.create(test)
            .expectComplete()
            .verify()
    }
}