package com.qingzhu.imaccess.controller

import com.ecwid.consul.v1.ConsulClient
import com.ecwid.consul.v1.QueryParams
import com.qingzhu.imaccess.config.WebSocketConfigProperties
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyValueAndAwait


@RestController
class WebSocketAddressController(
    val webSocketConfigProperties: WebSocketConfigProperties,
    val consulClient: ConsulClient
) {
    data class WebSocketAddress(
        val address: String,
        val port: Int
    )

    suspend fun getWebSocketAddress(serverRequest: ServerRequest): ServerResponse {
        val webSocketName = serverRequest.queryParam("serviceName").orElseGet { webSocketConfigProperties.name }
        val catalogService = consulClient.getCatalogService(webSocketName, QueryParams.DEFAULT)
        return ok().contentType(APPLICATION_JSON)
            .bodyValueAndAwait(catalogService.value.map { WebSocketAddress(it.serviceAddress, it.servicePort) }
                .toList())
    }
}