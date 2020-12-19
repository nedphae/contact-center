package com.qingzhu.imaccess.socketio

import com.corundumstudio.socketio.AckCallback
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.common.message.Header
import com.qingzhu.imaccess.domain.view.WebSocketResponse
import kotlinx.coroutines.*
import org.springframework.http.HttpStatus
import java.util.concurrent.TimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class AckBuilder<T>(
        private var request: AckRequest
) {
    private var header: Header? = null
    private var httpStatus: HttpStatus = HttpStatus.OK
    private var body: T? = null

    fun header(header: Header) = apply { this.header = header }
    fun httpStatus(httpStatus: HttpStatus) = apply { this.httpStatus = httpStatus }
    fun body(body: T?) = apply { this.body = body }

    fun send() {
        if (header == null) throw IllegalArgumentException("header can`t be null.")
        messageAck(request, WebSocketResponse(header!!, httpStatus.value(), body))
    }
}

/**
 * --------------- ack ---------------
 * 回复消息，使用 JSON 空值过滤
 */
fun <T> messageAck(request: AckRequest, webSocketResponse: WebSocketResponse<T>) {
    if (request.isAckRequested) {
        request.sendAckData(webSocketResponse)
    }
}

fun errorAck(request: AckRequest, header: Header, throwable: Throwable) {
    AckBuilder<String>(request).header(header)
            .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(throwable.message).send()
    throwable.printStackTrace()
}

fun messageAck(request: AckRequest, header: Header) {
    AckBuilder<Unit>(request).header(header).send()
}

/**
 * --------------- sync ---------------
 * Attention:
 *  this call will block your fun until it return
 *  so don't use in batch operation
 */
fun SocketIOClient.sendAndAwait(event: String, data: Any): WebSocketResponse<*> {
    val client = this
    return runBlocking(Dispatchers.IO) {
        client.syncSend(event, data)
    }
}

private fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (this.isActive) this.resume(value)
}

private fun <T> CancellableContinuation<T>.resumeWithExceptionIfActive(exception: Throwable) {
    if (this.isActive) this.resumeWithException(exception)
}

private suspend fun SocketIOClient.syncSend(event: String, data: Any) =
        suspendCancellableCoroutine { cont: CancellableContinuation<WebSocketResponse<*>> ->
            this.sendWithCallback(event, data, {
                cont.resumeWithExceptionIfActive(TimeoutException("响应超时"))
            }) {
                cont.resumeIfActive(it)
            }
            GlobalScope.launch {
                delay(15050)
                cont.resumeWithExceptionIfActive(TimeoutException("响应超时"))
            }
        }

// --------------- async ---------------

fun SocketIOClient.sendWithCallback(event: String, data: Any, onTimeout: () -> Unit = {}, onSuccess: (data: WebSocketResponse<*>) -> Unit = {}) {
    this.sendEvent(event,
            object : AckCallback<WebSocketResponse<*>>(WebSocketResponse::class.java, 15000) {
                override fun onSuccess(result: WebSocketResponse<*>?) {
                    result?.let { onSuccess(result) }
                }

                override fun onTimeout() {
                    onTimeout()
                }
            }, data)
}
