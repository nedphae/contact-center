package com.qingzhu.imaccess.util

import arrow.core.extensions.list.foldable.isNotEmpty
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

internal class FluxTest {

    @Test
    fun testContextWriteWithDefer() {
        val atomicLastId = AtomicLong(1)
        Flux.interval(Duration.ofSeconds(5))
            .flatMap {
                println("第: ${it + 1} 次")
                val lastId = atomicLastId.get()
                Mono.just(listOf(1 * lastId, 2 * lastId, 3 * lastId))
                    .doOnNext { list ->
                        atomicLastId.compareAndSet(lastId, list.last())
                        println(list)
                    }
            }
            .subscribe()
        Thread.sleep(5000 * 10)
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
        Mono.empty<Int>()
            .doOnNext {
                println(it)
            }
            .transform { text ->
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
                // Flux to asFlow
                .asFlow().toList()
            println(i)
        }
    }

    @Test
    fun testInterval() {
        Flux.interval(Duration.ZERO, Duration.ofSeconds(3), Schedulers.single())
            .doOnNext {
                println(it)
            }
            .then(Mono.just(1))
            .subscribe()
        Thread.sleep(9000)
    }
}