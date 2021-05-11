package com.qingzhu.gateway.loadbalancer

import org.apache.commons.logging.LogFactory
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.DefaultResponse
import org.springframework.cloud.client.loadbalancer.EmptyResponse
import org.springframework.cloud.client.loadbalancer.Response
import org.springframework.cloud.consul.discovery.reactive.ConsulReactiveDiscoveryClient
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI
import kotlin.math.abs


/**
 * 获取服务未进行缓存
 * TODO 修改为 Spring [LoadBalancerClientConfiguration] 中的方式（使用 cache）
 */
@Component
class HashRobinLoadBalancer(
    val consulReactiveDiscoveryClient: ConsulReactiveDiscoveryClient
) {
    companion object {
        private val log = LogFactory.getLog(HashRobinLoadBalancer::class.java)
    }

    fun choose(exchange: ServerWebExchange): Mono<Response<ServiceInstance>> {
        val uri = exchange.getAttribute<URI>(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)
        // return Mono.just(getInstanceResponse(exchange.request, consulDiscoveryClient.getInstances(uri!!.host)))
        //
        // return Mono.create {
        //     it.success(getInstanceResponse(exchange.request, consulDiscoveryClient.getInstances(uri!!.host)))
        // }
        return consulReactiveDiscoveryClient.getInstances(uri!!.host).collectList()
            .map { getInstanceResponse(exchange.request, it) }
        // 茴字有几种写法？
    }

    private fun getInstanceResponse(
        request: ServerHttpRequest,
        instances: List<ServiceInstance>
    ): Response<ServiceInstance> {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: hash load balancer")
            return EmptyResponse()
        }
        // IP HASH
        val pos: Int = abs(request.remoteAddress?.address?.hostAddress.hashCode())
        val instance = instances[pos % instances.size]
        return DefaultResponse(instance)
    }
}