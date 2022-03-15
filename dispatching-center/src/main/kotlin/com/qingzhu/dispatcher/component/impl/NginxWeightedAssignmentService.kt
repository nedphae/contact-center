package com.qingzhu.dispatcher.component.impl

import arrow.core.extensions.list.foldable.isNotEmpty
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.dispatcher.component.MessageService
import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import com.qingzhu.dispatcher.domain.entity.WeightInfo
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Nginx加权平均分配
 */
@Deprecated("使用随机算法替换")
@Component
class NginxWeightedAssignmentService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val messageService: MessageService
) {
    /**
     * 获取 客服权重分配
     */
    fun assignmentStaff(organizationId: Int, shuntId: Long): Mono<Long> {
        val opsForHash = redisTemplate.opsForHash<Long, String>()
        val key = "weight_info:${organizationId}:${shuntId}"
        val redisMap = opsForHash.entries(key)
            .mapValues { JsonUtils.objectMapper.readValue(it.value, WeightInfo::class.java) }
            .toMutableMap()
        val flux = messageService.findIdleStaff(organizationId, shuntId)
        return flux
            .collectList()
            .filter { it.isNotEmpty() }
            .flatMap { mutableList ->
                val (map, weightInfo) = getByList(shuntId, mutableList, redisMap)
                opsForHash.putAll(key, map.mapValues { JsonUtils.toJson(it.value) })
                Mono.justOrEmpty(weightInfo.get().staffId)
            }
    }

    private fun getByWeighted(weightInfoList: List<WeightInfo>): Optional<WeightInfo> {
        var total = 0
        var bestNo: WeightInfo? = null
        weightInfoList.parallelStream()
            .filter { it.weight > 0 }
            .peek { it.currentWeight += it.effectiveWeight }
            .peek { total += it.effectiveWeight }
            .filter { it.max > it.current }
            .filter { bestNo == null || it.currentWeight > bestNo!!.currentWeight }
            .forEach { bestNo = it }
        if (bestNo != null) {
            bestNo!!.currentWeight -= total
        }
        return Optional.ofNullable(bestNo)
    }

    /**
     * 本以为可以简单点实现，写到最后还是跟之前写的一样啊
     */
    private fun getByList(shuntId: Long, list: List<StaffDispatcherDto>, weightInfoMap: MutableMap<Long, WeightInfo>):
            Pair<MutableMap<Long, WeightInfo>, Optional<WeightInfo>> {
        val tempWeightInfoMap: MutableMap<Long, WeightInfo> = ConcurrentHashMap()
        list.parallelStream()
            .forEach {
                (weightInfoMap[it.staffId]?.apply {
                    this.max = it.maxServiceCount
                    this.current = it.currentServiceCount
                    this.weight = it.priorityOfShunt.second ?: 0
                } ?: WeightInfo(
                    it.organizationId,
                    it.staffId,
                    it.priorityOfShunt.second ?: 0,
                    it.maxServiceCount,
                    it.currentServiceCount
                )).apply {
                    tempWeightInfoMap[this.staffId] = this
                }
            }
        return tempWeightInfoMap to getByWeighted(tempWeightInfoMap.values.toList())
    }
}