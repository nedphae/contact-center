package com.qingzhu.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@Component
class AccessGatewayFilter(
        val reactiveJwtDecoder: ReactiveJwtDecoder) : GlobalFilter {
    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     * @param exchange webflux 契约
     * @param chain 过滤器链
     * @return
     */
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val ignoreUrls = listOf("/oauth/token", "/socket.io/")
        if (ignoreUrls.contains(exchange.request.path.value())){
            return chain.filter(exchange)
        }
        // 解析 jwt, 获取机构 id 保存到 http request parameter
        // 非阻塞写法
        val auth = exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION) ?: return unauthorized(exchange)
        val decode = reactiveJwtDecoder.decode(auth)
        return decode.map { it.claims }
                .doOnNext {
                    exchange.request.queryParams["organizationId"] = it["oid"]?.toString()
                    exchange.request.queryParams["staffId"] = it["sid"]?.toString()
                    it["oid"]?.toString()
                }
                .flatMap {
                    if (it.isNullOrEmpty()) {
                        unauthorized(exchange)
                    }
                    chain.filter(exchange)
                }
    }

    /**
     * 网关拒绝，返回401
     *
     * @param exchange webflux 契约
     */
    private fun unauthorized(exchange: ServerWebExchange): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        val buffer: DataBuffer = exchange.response
                .bufferFactory().wrap(HttpStatus.UNAUTHORIZED.reasonPhrase.toByteArray())
        return exchange.response.writeWith(Flux.just(buffer))
    }
}