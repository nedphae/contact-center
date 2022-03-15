package com.qingzhu.dispatcher.component.impl

import arrow.core.extensions.list.foldable.isNotEmpty
import com.qingzhu.dispatcher.component.AssignmentInterface
import com.qingzhu.dispatcher.domain.dto.StaffDispatcherDto
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.security.SecureRandom

/**
 * 随机加权平均分配
 * 效率高，但是达不到绝对平均
 */
@Component
class WeightedAssignmentService : AssignmentInterface {
    override fun assignmentStaff(flux: Flux<StaffDispatcherDto>): Mono<Long> {
        return flux
            .collectList()
            .filter { it.isNotEmpty() }
            .flatMap {
                val solution = Solution(it)
                Mono.justOrEmpty(it[solution.pickIndex()].staffId)
            }
    }

    private class Solution(staffDispatcherDtoList: List<StaffDispatcherDto>) {
        companion object {
            private val rand = SecureRandom()
        }

        private val sum = ArrayList<Int>()
        private var tot = 0

        init {
            staffDispatcherDtoList.forEach {
                tot += it.priorityOfShunt.second ?: 0
                sum.add(tot)
            }
        }

        fun pickIndex(): Int {
            val tag = rand.nextInt(tot)

            var low = 0
            var high = sum.size - 1
            // 查询满足 tag < p[x] 的最小下标。
            while (low != high) {
                val mid = (low + high) / 2
                // 二分查找
                if (tag >= sum[mid]) low = mid + 1
                else high = mid
            }
            return low
        }
    }
}