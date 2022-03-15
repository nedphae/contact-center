package com.qingzhu.messageserver

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.map.listener.EntryExpiredListener
import com.hazelcast.map.listener.EntryRemovedListener
import com.qingzhu.messageserver.config.CacheManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.function.StreamBridge
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import reactor.core.publisher.Sinks
import java.util.concurrent.TimeUnit


/**
 * Kotlin Y 组合实现
 *
 * lambda f. (lambda x. (f(x x)) lambda x. (f(x x)))
 *
 * FP YCombinator
 */

// 为了方便易懂，使用 X 用做函数 (X)->Int 的别名
typealias X = (Int) -> Int

typealias F = Function1<X, X>

// G 递归引用 G 自己
interface G : Function1<G, X>

// create a fun G from lazy blocking
fun G(block: (G) -> X) = object : G {
    // 调用函数自身 `block(g)` like as `g(g)`
    override fun invoke(g: G) = block(g)
}

fun Y(f: F): X {
    val p1 = G { g -> f { x -> g(g)(x) } }
    return p1(p1)
}

val fact = Y { rec ->
    { n ->
        if (n == 0) 1 else n * rec(n - 1)
    }
}
val fib = Y { f ->
    { n ->
        if (n == 1 || n == 2) 1 else f(n - 1) + f(n - 2)

    }
}

@SpringBootTest(classes = [MessageServerApplication::class])
class MessageServerApplicationTests {

    @Autowired
    @Qualifier("hazelcastInstance")
    private lateinit var hazelcastInstance: HazelcastInstance

    @Test
    fun testEntryListener() {
        val map = hazelcastInstance.getMap<String, String>("test")
        map["key1"] = "value1"
        map.put("key2", "value2", 2, TimeUnit.SECONDS)
        map.addEntryListener(EntryRemovedListener<String, String> {
            println("删除事件")
            println(it)
        }, false)
        map.addEntryListener(EntryExpiredListener<String, String> {
            println("过期事件")
            println(it)
        }, true)
        map.remove("key1")
        Thread.sleep(1000 * 1)
        map.setTtl("key2", 3, TimeUnit.SECONDS)
        Thread.sleep(1000 * 2)
        println("更新过期时间")
        Thread.sleep(1000 * 10)
    }

    @Test
    fun testHazelcast() {
        val cacheHazelcastInstance = CacheManager.hazelcastInstance
        val lock = hazelcastInstance.cpSubsystem.getLock("test")
        try {
            lock.lock()
            println("lock 成功")
        } finally {
            lock.unlock()
        }
        assertEquals(cacheHazelcastInstance, hazelcastInstance)
        Thread.sleep(1000 * 100)
    }

    @Autowired
    private lateinit var streamBridge: StreamBridge

    @Autowired
    private lateinit var many: Sinks.Many<Message<String>>

    /**
     * 测试 processor 生产消息
     */
    // @Autowired
    @Test
    fun message() {
        // Executors.newSingleThreadExecutor().submit {
        var i = 0
        while (i < 100) {
            val str = "测试分区1: $i"
            val message = MessageBuilder.withPayload(str)
                // 选择分区
                // .setHeader(KafkaHeaders.PARTITION_ID, 1)
                // 设置分区 key
                // .setHeader("partitionKey", str.hashCode() % 4)
                .setHeader(KafkaHeaders.MESSAGE_KEY, "bar".encodeToByteArray())
                .build()
            // many.tryEmitNext(message)
            streamBridge.send("im.test", message)
            i++
            if (i % 10 == 0) {
                Thread.sleep(1000)
                println("next 10")
            }
        }
        // }
    }

    @Test
    fun contextLoads() {
        // (lambda fn:
        // (lambda f: f(f))(lambda f:
        //         fn(lambda n: f(f)(n))
        // )
        //         )(lambda g:
        //         lambda n: 1 if n in [1, 2] else g(n-1) + g(n-2)
        // )(10)

        // (function (callable $fn): callable {
        //     return (function (callable $f): callable {
        //     return $f($f);
        // })(function (callable $f) use ($fn): callable {
        //     return $fn(function (int $n) use ($f): int {
        //     return $f($f)($n);
        // });
        // });
        // })(function (callable $g): callable {
        //     return function (int $n) use ($g): int {
        //     return in_array($n, [1, 2]) ? 1 : $g($n - 1) + $g($n - 2);
        // };
        // })(10);

        // (fn =>
        // (f => f(f))(f => fn(n => f(f)(n)))
        // )(g =>
        // n => [1, 2].indexOf(n) > -1 ? 1 : g(n - 1) + g(n - 2)
        // )(10);

        var lambda2: (Int) -> Int = { it }
        lambda2 = { if (it <= 2) 1 else lambda2(it - 1) + lambda2(it - 2) }
        // 通过 typealias 定义一些类型可以写成 Y 组合的 lambda 递归
        // 太麻烦了
    }

    fun efg(a: String, b: String, c: String) {
        println("$a + $b + $c")
    }

    fun efg1(a: String) = fun(b: String) = fun(c: String) {
        efg(a, b, c)
    }
}
