package com.qingzhu.messageserver.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import com.qingzhu.messageserver.MessageServerApplicationTests
import com.qingzhu.messageserver.domain.entity.Conversation
import org.apache.lucene.search.join.ScoreMode
import org.elasticsearch.index.query.QueryBuilders
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder
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

    @Test
    fun testSearch() {
        val searchTest = "客户"
        val andQuery = QueryBuilders.boolQuery()
            .should(QueryBuilders.multiMatchQuery(searchTest))
            .should(QueryBuilders.nestedQuery("chatMessages", QueryBuilders.multiMatchQuery(searchTest), ScoreMode.Avg))
        println(andQuery)
        println(QueryBuilders.multiMatchQuery(searchTest))

        // QueryBuilders.rangeQuery("").gte(12)
        val termQuery = QueryBuilders.termsQuery("staffId", *listOf(1, 2).toIntArray())
        val query = NativeSearchQueryBuilder()
            .withQuery(andQuery)
            .withFilter(termQuery)
            .withPageable(PageRequest.of(1, 2))
            .build()

        val result = reactiveElasticsearchTemplate.searchForPage(query, Conversation::class.java)
        val test = result
            .doOnNext { println(it.toJson()) }
            .map { PageImpl(it.content, it.pageable, it.totalElements) }
            .map { it.toJson() }
            .doOnNext { println(it) }
            .map { JsonUtils.fromJson<LinkedHashMap<String, Any>>(it) }
            .map {
                println(it)
            }
        StepVerifier.create(test)
            .expectNextCount(1)
            .verifyComplete()
    }
}