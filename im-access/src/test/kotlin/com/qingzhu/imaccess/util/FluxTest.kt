package com.qingzhu.imaccess.util

import arrow.core.extensions.list.foldable.isNotEmpty
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class FluxTest {

    @Test
    fun testContext() {
        Mono.just("Hello")
            .flatMap {
                Mono.deferContextual { ctx ->
                    println(ctx.get("test") as String)
                    Mono.just(it + " " + ctx.get("test"))
                }
            }
            .transformDeferredContextual { t, u ->
                println(u.get("test") as String)
                t
            }
            .map { "$it World" }
            // contextWrite 必须在使用该 context 的后面
            .contextWrite { it.put("test", "testContext") }
            .subscribe {
                println("subscribe: $it")
            }
    }

    @Test
    fun testThen() {
        var start = 1
        Mono.create<Int> {
            it.success(start)
            it.success(start + 1)
        }
            .doOnNext {
                println(it)
            }
            .or(Mono.just(2))
            .subscribe {
                println("subscribe:${it}")
            }
    }

    @Test
    fun testFlux() {
        Flux
            .empty<Int>()
            .collectList()
            .filter { it.isNotEmpty() }
            .switchIfEmpty(Mono.just(listOf(5, 6, 7, 8)))
            .subscribe {
                println(it)
            }
        val distinct = Flux
            .just(1, 1, 2, 3, 4, 4)
            .distinct()
            .collectList()
        StepVerifier.create(distinct)
            .assertNext {
                println(it)
                Assertions.assertIterableEquals(listOf(1, 2, 3, 4), it)
            }
            .verifyComplete()
    }

    /**
     * 如示例，transform 返回自身时 中间的 flatMap 会优化掉
     */
    @Test
    fun testTwoTransform() {
        Mono.just(1)
                .doOnNext {
                    println(it)
                }
                .transform {
                    text ->
                    text.flatMap {
                        println(2)
                        Mono.just(2)
                    }.transform { text }
                }
                .doOnNext {
                    println(it)
                }
                .subscribe()
    }

    @Test
    fun testAwait() {
        runBlocking {
            val i = Flux
                    .just(1, 1, 2, 3, 4, 4)
                    .asFlow().toList()
            println(i)
        }
    }
}