package com.qingzhu.imaccess.util

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

// 计数器 Actor 的各种类型
sealed class CounterMsg
object IncCounter : CounterMsg() // 递增计数器的单向消息
class GetCounter(val response: CompletableDeferred<Int>) : CounterMsg() // 携带回复的请求

internal class ActorTest {
    private suspend fun massiveRun(action: suspend () -> Unit) {
        val n = 100  // 启动的协程数量
        val k = 1000 // 每个协程重复执行同个动作的次数
        val time = measureTimeMillis {
            coroutineScope { // 协程的作用域
                repeat(n) {
                    launch {
                        repeat(k) { action() }
                    }
                }
            }
        }
        println("Completed ${n * k} actions in $time ms")
    }

    // 这个函数启动一个新的计数器 actor
    private fun CoroutineScope.counterActor() = actor<CounterMsg> {
        var counter = 0 // actor 状态
        for (msg in channel) { // 即将到来消息的迭代器
            when (msg) {
                is IncCounter -> counter++
                is GetCounter -> msg.response.complete(counter)
            }
        }
    }

    private fun main() = runBlocking<Unit> {
        val counter = counterActor() // 创建该 actor
        withContext(Dispatchers.Default) {
            massiveRun {
                counter.send(IncCounter)
            }
        }
        // 发送一条消息以用来从一个 actor 中获取计数值
        val response = CompletableDeferred<Int>()
        counter.send(GetCounter(response))
        println("Counter = ${response.await()}")
        counter.close() // 关闭该actor
    }

    @Test
    fun testActor() {
        main()
    }
}