package com.qingzhu.gateway

import com.qingzhu.gateway.config.SecurityConfiguration
import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class TestCoroutine {
    @Test
    fun testCoroutine() {
        val launch = GlobalScope.launch {
            // 在后台启动一个新的协程并继续
            delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("World!") // 在延迟后打印输出
        }
        println("Hello,") // 协程已在等待时主线程还在继续

        Thread.sleep(1000L) // 阻塞主线程 1 秒钟来保证 JVM 存活
        runBlocking {
            // 但是这个表达式阻塞了主线程
            launch.join()   // 等待 launch 结束
            delay(1000L)  // ……我们延迟 1 秒来保证 JVM 的存活
        }
        val (_, b) = getPair()
        println(b)
    }

    @Test
    fun testFlux() {
        val reactiveJwtDecoder = SecurityConfiguration().reactiveJwtDecoder()
        val decode =
            reactiveJwtDecoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6OTQ5MSwiYXVkIjpbImNzLWFkbWluIiwiYm90Iiwib2F1dGgyIl0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsidGVzdCJdLCJleHAiOjE1ODQ2MTI1NTIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiMjM1YmQxMTUtNWIwOS00ZjlkLTk3YzctZmQ4OGEyMDQ4NjkwIiwiY2xpZW50X2lkIjoidXNlcl9jbGllbnQifQ.F_wZxxo14AvwdlrkmrF0-MZMYkd0WLUZITX7FOkpW1c6MFwZLw09f5i8l2BzF-yCI_nTjTFLykKejJl7L7vkaZhABHfEf8NKV5ZVdaVaAmGga-AXqHmvHEwh3guvgn5R552Z5lY9bzq0dDig_vwuT-9E9_T_KYXP7WM2caCj76gqO-iYNfrMkZoRVMIiVUU-psmum-bnFoIE08Yb79DMNx27KIIC2M89XNUKMGwkaRkjlHEOWyVtxaNNZNIYWyTdIEUqPdbX4r2QSJnfQDGWoEEde3W4t2Xso6kPmG-ctZrDEqug6esNQ8F2v7AC2GLP9SOA8QXcpN-RUYQPQJtjIg")
        decode.map { it.claims["organizationId"] as Long }
            .subscribe {
                println(it)
            }
    }

    @Test
    fun testFluxWithoutSubscribe() {
        val reactiveJwtDecoder = SecurityConfiguration().reactiveJwtDecoder()
        val decode =
            reactiveJwtDecoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6OTQ5MSwiYXVkIjpbImNzLWFkbWluIiwiYm90Iiwib2F1dGgyIl0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsidGVzdCJdLCJleHAiOjE1ODQ2MTI1NTIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiMjM1YmQxMTUtNWIwOS00ZjlkLTk3YzctZmQ4OGEyMDQ4NjkwIiwiY2xpZW50X2lkIjoidXNlcl9jbGllbnQifQ.F_wZxxo14AvwdlrkmrF0-MZMYkd0WLUZITX7FOkpW1c6MFwZLw09f5i8l2BzF-yCI_nTjTFLykKejJl7L7vkaZhABHfEf8NKV5ZVdaVaAmGga-AXqHmvHEwh3guvgn5R552Z5lY9bzq0dDig_vwuT-9E9_T_KYXP7WM2caCj76gqO-iYNfrMkZoRVMIiVUU-psmum-bnFoIE08Yb79DMNx27KIIC2M89XNUKMGwkaRkjlHEOWyVtxaNNZNIYWyTdIEUqPdbX4r2QSJnfQDGWoEEde3W4t2Xso6kPmG-ctZrDEqug6esNQ8F2v7AC2GLP9SOA8QXcpN-RUYQPQJtjIg")
        decode.map { it.claims["organizationId"] as Long }
            .map {
                println(it)
            }
            .subscribe()
    }

    @InternalCoroutinesApi
    @Test
    fun testCoroutineWithFlux() {
        val reactiveJwtDecoder = SecurityConfiguration().reactiveJwtDecoder()
        runBlocking {
            setParams(reactiveJwtDecoder)
        }
    }

    @InternalCoroutinesApi
    private suspend fun setParams(reactiveJwtDecoder: ReactiveJwtDecoder) =
        suspendCancellableCoroutine { cont: CancellableContinuation<Unit> ->
            // val decode = reactiveJwtDecoder.decode("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJvcmdhbml6YXRpb25JZCI6OTQ5MSwiYXVkIjpbImNzLWFkbWluIiwiYm90Iiwib2F1dGgyIl0sInVzZXJfbmFtZSI6ImFkbWluIiwic2NvcGUiOlsidGVzdCJdLCJleHAiOjE1ODQ2MTI1NTIsImF1dGhvcml0aWVzIjpbIlJPTEVfQURNSU4iXSwianRpIjoiMjM1YmQxMTUtNWIwOS00ZjlkLTk3YzctZmQ4OGEyMDQ4NjkwIiwiY2xpZW50X2lkIjoidXNlcl9jbGllbnQifQ.F_wZxxo14AvwdlrkmrF0-MZMYkd0WLUZITX7FOkpW1c6MFwZLw09f5i8l2BzF-yCI_nTjTFLykKejJl7L7vkaZhABHfEf8NKV5ZVdaVaAmGga-AXqHmvHEwh3guvgn5R552Z5lY9bzq0dDig_vwuT-9E9_T_KYXP7WM2caCj76gqO-iYNfrMkZoRVMIiVUU-psmum-bnFoIE08Yb79DMNx27KIIC2M89XNUKMGwkaRkjlHEOWyVtxaNNZNIYWyTdIEUqPdbX4r2QSJnfQDGWoEEde3W4t2Xso6kPmG-ctZrDEqug6esNQ8F2v7AC2GLP9SOA8QXcpN-RUYQPQJtjIg")
            // decode.map { it.claims["organizationId"] as Long }
            //         .subscribe {
            //             println(it)
            //         }
            Thread({
                println("新线程睡眠")
                cont.tryResumeWithException(TimeoutException("消息超时"))?.let { cont.completeResume(it) }
                cont.tryResumeWithException(TimeoutException("消息超时"))?.let { cont.completeResume(it) }
                if (cont.isActive) cont.resumeWithException(TimeoutException("消息超时"))
                if (cont.isActive) cont.resumeWithException(TimeoutException("消息超时"))
                Thread.sleep(10000L)
                println("睡眠结束")
                if (cont.isActive) cont.resume(Unit)
            }).start()
        }

    @Test
    fun testAsync() {
        runBlocking {
            val result = async {
                Thread({
                    println("新线程睡眠")
                    Thread.sleep(10000L)
                    println("睡眠结束")
                }).start()
            }
            result.await()
        }
    }

    private fun getPair(): Pair<String, String> {
        return Pair("Hello", "World!")
    }
}