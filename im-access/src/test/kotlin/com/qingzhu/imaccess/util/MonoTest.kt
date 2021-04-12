package com.qingzhu.imaccess.util

import com.qingzhu.imaccess.domain.constant.CreatorType
import com.qingzhu.imaccess.domain.constant.MessageType
import com.qingzhu.imaccess.domain.value.Content
import com.qingzhu.imaccess.domain.value.Message
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

internal class MonoTest {
    @Test
    fun testConcatWith() {
        val from = Mono.just(true)
        Mono.just(true)
                .filter { !it }
                .switchIfEmpty(from)
                .map {
                    println(it)
                    it
                }
                // .reduce { t: String?, _: String? -> t }
                .subscribe { println(it) }

        Mono.create<Int> {
            it.success() // aka it.success(null)
        }
                .flatMap {
                    println("flatMap")
                    Mono.just(2)
                }
                .switchIfEmpty(Mono.just(3))
                .map { println(it);it }
                .subscribe { println(it) }
    }

    @Test
    fun testMonoDispose() {
        val disposable = Flux
                .interval(Duration.ofSeconds(1))
                .subscribe {
                    println(it)
                }
        Thread.sleep(10000L)
        disposable.dispose()
        Thread.sleep(5000L)
    }

    @Test
    fun testMono() {
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
                .filter { it.uuid != "1" }
                .doOnNext {
                    println("未过滤：$it")
                }
                .cache()
                .doOnDiscard(Message::class.java) {
                    println("过滤：$it")
                }
                .subscribe {
                    println("订阅：$it")
                }
    }

    private fun createCache(): Mono<Int> {
        return Mono.create<Int> {
            print("no cache: ")
            it.success(1)
        }.cache()
    }

    @Test
    fun testCache() {
        val cache = createCache()
        cache.subscribe { println(it) }
        cache.subscribe { print("cache?: "); println(it) }
    }

    @Test
    fun testOnErrorResume() {
        Mono.just(1)
                .doOnNext { println(it) }
                .then(Mono.just(1).map { it / 0 })
                .onErrorResume {
                    Mono.empty()
                }
                .switchIfEmpty(Mono.just(2))
                .subscribe(::println)


        Mono.just(1)
                .doOnNext { num -> Mono.just(num).map { it / 0 }.subscribe { println(it) } }
                .onErrorResume {
                    Mono.empty()
                }
                .switchIfEmpty(Mono.just(2))
                .subscribe(::println)
    }
}