package com.qingzhu.imaccess.util

import arrow.aql.extensions.list.select.query
import arrow.aql.extensions.list.select.value
import arrow.aql.extensions.listk.select.select
import arrow.aql.extensions.listk.where.where
import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicative.map
import arrow.core.extensions.eq
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.option.monadError.monadError
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.Resource
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.async.async
import arrow.fx.extensions.io.bracket.bracket
import arrow.fx.extensions.resource.monad.monad
import arrow.fx.fix
import arrow.fx.typeclasses.Fiber
import arrow.typeclasses.Eq
import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Test

class TestArrow {

    @Test
    fun testSelect() {
        listOf(1, 2, 3).partition { it > 2 }
        val result: List<Int> = listOf(1, 2, 3)
            .query {
                select { this + 1 }.where { this > 2 }
            }.value()
        println(result)
    }

    @Test
    fun testResource() {
        fun createInputStream(): IO<String> = IO { println("Start input"); "input" }
        fun createOutputStream(input: String): IO<String> =
            IO { println("Start output"); println("Start copy: $input\toutput");"output" }

        fun closeInputStream(input: String): IO<Unit> = IO.fx { print(input); println("Closed input") }
        fun closeOutputStream(output: String): IO<Unit> = IO.fx { print(output); println("Closed output") }
        val managedTProgram = Resource.monad(IO.bracket()).fx.monad {
            val inputResource = Resource(::createInputStream, ::closeInputStream, IO.bracket()).bind()
            Resource({ createOutputStream(inputResource) }, ::closeOutputStream, IO.bracket()).bind()
        }.fix().use { IO.unit }.fix()
        managedTProgram.unsafeRunSync()
    }

    fun testNever() {
        println(Long.MAX_VALUE)
        val result: IO<Int> = IO.never
        println("start")
        println(result.unsafeRunSync())
    }

    @Test
    fun testFX() {
        println("1、${Thread.currentThread().id}")
        IO {
            Thread.sleep(4000)
            println("2、${Thread.currentThread().id}")
            0
        }
            .map {
                println("3、${Thread.currentThread().id}")
                it
            }
            .fork(Dispatchers.Default)
            .flatMap {
                it.join()
                // IO.fx {
                //     it.join().bind()
                // }
            }
            // .unsafeRunSync()
            .unsafeRunAsync {
                println("4、${Thread.currentThread().id}")
            }
        // .unsafeRunAsync { result ->
        //     println("4、${Thread.currentThread().id}")
        //     result.fold({ println("Error") }, { println(it) })
        // }
        // 异步 非多线程
        IO.async()
            .async { callback: (Either<Throwable, Int>) -> Unit ->
                println("6、${Thread.currentThread().id}")
                callback(1.right())
            }.fix().attempt().unsafeRunSync()

        println("5、${Thread.currentThread().id}")
        Thread.sleep(5000)
    }

    @Test
    fun testCore() {
        val stringEq = String.eq()
        println(stringEq.run {
            "1".eqv("2")
                    && "2".neqv("1")
        })
        Eq.any().run { Some(1).eqv(Option.just(1)) }
        val intEq = Eq<Int> { a, b -> a == b }
        intEq.run { 1.eqv(2) }

        fun <F> List<F>.filter(other: F, EQ: Eq<F>) = this.filter { EQ.run { it.eqv(other) } }
        // 编译器 类型检查
        listOf("1", "2", "3").filter("2", String.eq())

        // 协程包裹的 IO monad
        IO { 0 }
            .flatMap { IO { it * 2 } }
            .map { it + 1 }
            .map { println(it); it }
            .runAsync { IO.never }
            .unsafeRunSync()

        Option(12)
        Option.monadError()

        listOf(Right(1), Right(2), Right(3)).sequence(Either.applicative<Throwable>()).map {
            println(it)
        }
    }

    @Test
    fun testFiber() {
        fun <A, B, C> parallelMap(
            first: IO<A>,
            second: IO<B>,
            f: (A, B) -> C
        ): IO<C> =
            IO.fx {
                val fiberOne: Fiber<ForIO, A> = first.fork(Dispatchers.Default).bind()
                val fiberTwo: Fiber<ForIO, B> = second.fork(Dispatchers.Default).bind()
                f(!fiberOne.join(), !fiberTwo.join())
            }

        val first = IO { Thread.sleep(5000) }.map {
            println("Hi, I am first")
            1
        }

        val second = IO { Thread.sleep(5000) }.map {
            println("Hi, I am second")
            2
        }
        parallelMap(first, second, Int::plus)
            //.unsafeRunSync().also { println(it) }
            .unsafeRunAsync { result ->
                result.fold({ println("Error") }, { println(it) })
            }

        Thread.sleep(5000 * 3)
    }

    @Test
    fun testUnsafe() {
        IO<Int> { throw RuntimeException("Boom!") }
            .runAsync { result ->
                result.fold({ IO { println("Error") } }, { IO { println(it.toString()) } })
            }
            .unsafeRunAsync {
                if (it.isRight()) {
                    println(it)
                }
            }
    }
}