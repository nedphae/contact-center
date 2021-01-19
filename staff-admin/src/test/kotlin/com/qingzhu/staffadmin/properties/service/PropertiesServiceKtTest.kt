package com.qingzhu.staffadmin.properties.service

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier

internal class PropertiesServiceKtTest {
    @Test
    fun testProperties() {
        val list = listOf(Properties(1, 9491, "sys.auto-reply.test", "21", "test"),
                Properties(2, 9491, "sys.auto-reply.test1", "20", "test"))
        val properties = createMapFromProperties(list.toFlux())
                .doOnNext {
                    println(it)
                }
        StepVerifier.create(properties)
                .expectNext("""{"sys":{"auto-reply":{"test1":{"id":"2","value":"20"},"test":{"id":"1","value":"21"}}}}""")
                .verifyComplete()
    }

    @Test
    fun testVerify() {
        val source: Flux<String> = Flux.just("John", "Monica", "Mark", "Cloe", "Frank", "Casper", "Olivia", "Emily", "Cate")
                .filter { name -> name.length == 4 }
                .map(String::toUpperCase)

        StepVerifier
                .create(source)
                .expectNext("JOHN")
                .expectNextMatches { name: String -> name.startsWith("MA") }
                .expectNext("CLOE", "CATE")
                .expectComplete()
                .verify()
    }
}