package com.qingzhu.gateway.filter

import com.qingzhu.gateway.loadbalancer.HashRobinLoadBalancer
import org.apache.commons.logging.LogFactory
import org.springframework.cloud.client.ServiceInstance
import org.springframework.cloud.client.loadbalancer.LoadBalancerUriTools
import org.springframework.cloud.client.loadbalancer.Response
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter
import org.springframework.cloud.gateway.support.DelegatingServiceInstance
import org.springframework.cloud.gateway.support.NotFoundException
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI


/**
 * 自定义实现 hash 负载均衡
 */
@Component
class ReactiveConsistentHashLoadBalancer(
    private val hashLoadBalancer: HashRobinLoadBalancer,
    private val properties: GatewayLoadBalancerProperties
) : GlobalFilter, Ordered {

    companion object {
        private const val LOAD_BALANCER_CLIENT_FILTER_ORDER = 77777
        private val log = LogFactory.getLog(ReactiveLoadBalancerClientFilter::class.java)
    }

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val url = exchange.getAttribute<URI>(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR)
        val schemePrefix = exchange.getAttribute<String>(ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR)
        // hlb for hash load balance
        if (url == null
            || "hlb" != url.scheme && "hlb" != schemePrefix
        ) {
            return chain.filter(exchange)
        }
        return choose(exchange).doOnNext { response: Response<ServiceInstance> ->
            if (!response.hasServer()) {
                throw NotFoundException.create(
                    properties.isUse404,
                    "Unable to find instance for " + url.host
                )
            }
            val uri = exchange.request.uri

            var overrideScheme: String? = null
            if (schemePrefix != null) {
                overrideScheme = url.scheme
            }
            val serviceInstance = DelegatingServiceInstance(
                response.server, overrideScheme
            )
            val requestUrl = LoadBalancerUriTools.reconstructURI(serviceInstance, uri)
            if (log.isTraceEnabled) {
                log.trace("ReactiveConsistentHashLoadBalancer url chosen: $requestUrl")
            }
            exchange.attributes[ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR] = requestUrl
        }.then(chain.filter(exchange))
    }

    private fun choose(exchange: ServerWebExchange): Mono<Response<ServiceInstance>> {
        return hashLoadBalancer.choose(exchange)
    }

    override fun getOrder(): Int {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER
    }
}