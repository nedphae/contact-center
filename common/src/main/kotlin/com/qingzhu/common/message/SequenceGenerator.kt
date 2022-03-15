package com.qingzhu.common.message

interface SequenceGenerator {
    /**
     * 生成 自增 唯一 id
     */
    fun getNextSequenceId(): Long
}