package com.qingzhu.imaccess.util

import arrow.core.extensions.list.foldable.isNotEmpty
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
                println(it)
            }
    }

    @Test
    fun testThen() {
        val start: Int? = null
        Mono.create<Int> {
            // it.success(start)
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
        Flux
            .just(1, 2, 3, 4)
            .collectList()
            .subscribe {
                println(it)
            }
    }
}