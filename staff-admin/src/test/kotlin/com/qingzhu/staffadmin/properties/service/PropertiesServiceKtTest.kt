package com.qingzhu.staffadmin.properties.service

import com.qingzhu.staffadmin.properties.domain.entity.Properties
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier

internal class PropertiesServiceKtTest {
    @Test
    fun testProperties() {
        val list = listOf(
            Properties(1, "sys", null, label = "系统配置"),
            Properties(2, "sys.autoReply", null, label = "自动回复"),
            Properties(3, "sys.autoReply.test", "21", "test"),
            Properties(4, "sys.autoReply.test1", "20", "test"),
        ).onEach {
            it.organizationId = 9491
        }
        val properties = createMapFromProperties(list.toFlux())
            .doOnNext {
                println(it)
            }
        StepVerifier.create(properties)
            .expectNext("""{"sys":{"autoReply":{"id":"2","test":{"label":"test","available":"true","value":"21","id":"3"},"available":"true","label":"自动回复","test1":{"id":"4","value":"20","available":"true","label":"test"}},"label":"系统配置","id":"1","available":"true"}}""")
            .verifyComplete()
    }

    @Test
    fun testVerify() {
        val source: Flux<String> =
            Flux.just("John", "Monica", "Mark", "Cloe", "Frank", "Casper", "Olivia", "Emily", "Cate")
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