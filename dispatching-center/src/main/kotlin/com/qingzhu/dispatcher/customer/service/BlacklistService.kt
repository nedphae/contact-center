package com.qingzhu.dispatcher.customer.service

import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import com.qingzhu.dispatcher.customer.domain.entity.Blacklist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import java.time.Instant

/**
 * 黑名单服务，仅仅保存在redis里
 * 每次查询删除已经失效的黑名单
 * TODO 后期可以添加持久化存储，用以审计
 */
@Service
class BlacklistService(
    redisTemplate: ReactiveRedisTemplate<String, String>
) {
    private val opsForHash = redisTemplate.opsForHash<String, String>()

    suspend fun saveBlacklist(blacklistFlow: Flow<Blacklist>): Flow<Blacklist> {
        val blacklist = blacklistFlow.toList()
        if (blacklist.isNotEmpty()) {
            val first = blacklist.first()
            val keyValue = blacklist.associate { "${it.preventStrategy}:${it.preventSource}" to it.toJson() }
            opsForHash.putAll("blacklist:${first.organizationId}", keyValue).awaitSingle()
        }
        return blacklistFlow
    }

    fun getAllBlacklist(organizationId: Int, audited: Boolean? = null): Flux<Blacklist> {
        val now = Instant.now()
        return opsForHash.scan("blacklist:${organizationId}")
            .map { JsonUtils.fromJson<Blacklist>(it.value) }
            .filter { if (audited == null) true else it.audited == audited }
            .collectList()
            .flatMapMany {
                val (effective, failure) = it.partition { bl -> bl.failureTime?.isAfter(now) ?: true }
                val removeKeyArray = failure.map { bl -> "${bl.preventStrategy}:${bl.preventSource}" }.toTypedArray()
                if (removeKeyArray.isNotEmpty()) {
                    opsForHash.remove(
                        "blacklist:${organizationId}",
                        *removeKeyArray
                    )
                        .thenMany(effective.toFlux())
                } else {
                    effective.toFlux()
                }
            }
    }

    fun getBlacklistBy(blacklist: Blacklist): Mono<Blacklist> {
        val now = Instant.now()
        return opsForHash.get(
            "blacklist:${blacklist.organizationId}",
            "${blacklist.preventStrategy}:${blacklist.preventSource}"
        )
            .map { JsonUtils.fromJson<Blacklist>(it) }
            .filter { it.failureTime == null || now.isBefore(it.failureTime) }
            .doOnDiscard(Blacklist::class.java) {
                // 如果已经失效就删除
                opsForHash.remove(
                    "blacklist:${it.organizationId}",
                    "${blacklist.preventStrategy}:${blacklist.preventSource}"
                )
            }
            .filter { (it.effectiveTime == null || now.isAfter(it.effectiveTime)) && it.audited }
    }

    suspend fun remove(organizationId: Int, blacklistFlow: Flow<Blacklist>): Long {
        val blacklist = blacklistFlow.toList()
        return if (blacklist.isNotEmpty()) {
            val keyValue = blacklist.map { "${it.preventStrategy}:${it.preventSource}" }.toTypedArray()
            opsForHash.remove(
                "blacklist:${organizationId}",
                *keyValue
            ).awaitSingle()
        } else 0
    }

}