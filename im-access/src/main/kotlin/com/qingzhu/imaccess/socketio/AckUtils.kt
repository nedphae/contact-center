package com.qingzhu.imaccess.socketio

import com.corundumstudio.socketio.AckCallback
import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.qingzhu.common.message.Header
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.imaccess.domain.view.WebSocketResponse
import com.qingzhu.imaccess.domain.view.WebSocketResponseWithString
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
 *
 * Reply message, use JSON null value filter
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
inline fun <reified T> SocketIOClient.sendAndAwait(event: String, data: Any): WebSocketResponse<T> {
    val client = this
    return runBlocking(Dispatchers.IO) {
        client.syncSend(event, data)
    }
}

fun <T> CancellableContinuation<T>.resumeIfActive(value: T) {
    if (this.isActive) this.resume(value)
}

fun <T> CancellableContinuation<T>.resumeWithExceptionIfActive(exception: Throwable) {
    if (this.isActive) this.resumeWithException(exception)
}

suspend inline fun <reified T> SocketIOClient.syncSend(event: String, data: Any) =
    suspendCancellableCoroutine { cont: CancellableContinuation<WebSocketResponse<T>> ->
        this.sendWithCallback<T>(event, data, {
            cont.resumeWithExceptionIfActive(TimeoutException("响应超时"))
        }) {
            cont.resumeIfActive(it)
        }
        GlobalScope.launch {
            delay(15050)
            cont.resumeWithExceptionIfActive(TimeoutException("响应超时"))
        }
    }

/** --------------- async --------------- */
inline fun <reified T> SocketIOClient.sendWithCallback(
    event: String, data: Any, crossinline onTimeout: () -> Unit = {},
    crossinline onSuccess: (data: WebSocketResponse<T>) -> Unit = {}
) {
    this.sendEvent(event,
        object : AckCallback<WebSocketResponseWithString>(WebSocketResponseWithString::class.java, 15000) {
            override fun onSuccess(result: WebSocketResponseWithString?) {
                result?.let {
                    // 先转换为 String， 然后再手动序列化
                    val body = result.body?.let { it1 -> JsonUtils.fromJson<T>(it1) }
                    onSuccess(WebSocketResponse(it.header, it.code, body))
                }
            }

            override fun onTimeout() {
                onTimeout()
            }
        }, data
    )
}
