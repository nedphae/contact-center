package com.qingzhu.dispatcher.service

import org.junit.jupiter.api.Test
import java.util.*


internal class NginxWeightedAssignmentServiceTest {

    @Test
    fun testNewWeightedAssignment() {
        val solution = Solution(intArrayOf(40, 30, 20, 10))
        val map = HashMap<Int, Int>()
        for (i in 1..10000) {
            map.compute(solution.pickIndex()) { _, v ->
                var temp = v ?: 0
                temp++
                return@compute temp
            }
        }
        println(map)
        map.clear()
        for (i in 1..10000) {
            val solutionForEach = Solution(intArrayOf(40, 30, 20, 10))
            map.compute(solutionForEach.pickIndex()) { _, v ->
                var temp = v ?: 0
                temp++
                return@compute temp
            }
        }
        println(map)
    }

    class Solution(w: IntArray) {

        private val psum = ArrayList<Int>()
        private var tot = 0
        private val rand = Random()

        init {
            w.forEach {
                tot += it
                psum.add(tot)
            }
        }

        fun pickIndex(): Int {
            val tag = rand.nextInt(tot)

            var lo = 0
            var hi = psum.size - 1
            while (lo != hi) {
                val mid = (lo + hi) / 2
                if (tag >= psum[mid]) lo = mid + 1
                else hi = mid
            }
            return lo
        }
    }
}