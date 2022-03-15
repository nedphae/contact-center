package com.qingzhu.imaccess.util

import arrow.fx.coroutines.Resource
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class NewTest {
    object Consumer
    object Handle

    class Service(val handle: Handle, val consumer: Consumer)

    suspend fun createConsumer(): Consumer = Consumer.also { println("Creating consumer") }
    suspend fun createDBHandle(): Handle = Handle.also { println("Creating db handle") }
    suspend fun createFancyService(consumer: Consumer, handle: Handle): Service =
        Service(handle, consumer).also { println("Creating service") }

    suspend fun closeConsumer(consumer: Consumer): Unit = println("Closed consumer")
    suspend fun closeDBHandle(handle: Handle): Unit = println("Closed db handle")
    suspend fun shutDownFancyService(service: Service): Unit = println("Closed service")

    val resourceProgram = suspend {
        Resource(::createConsumer, ::closeConsumer)
            .zip(Resource(::createDBHandle, ::closeDBHandle))
            .flatMap { (consumer, handle) ->
                Resource({ createFancyService(consumer, handle) }, { service -> shutDownFancyService(service) })
            }.use { service ->
                // use service
                // <...>
            }
    }

    @Test
    fun test() {
        runBlocking {
            resourceProgram.invoke()
        }
    }
}
