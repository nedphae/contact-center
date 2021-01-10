package com.qingzhu.imaccess.util

import arrow.core.extensions.list.foldable.isNotEmpty
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

internal class FluxTest {

    @Test
    fun testFlux() {
        Flux
                .empty<Int>()
                .collectList()
                .filter { it.isNotEmpty() }
                .switchIfEmpty(Mono.just(listOf(5,6,7,8)))
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
