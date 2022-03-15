package com.qingzhu.common.domain.shared

interface Specification<T> {
    fun isSatisfiedBy(t: T): Boolean

    fun and(specification: Specification<T>): Specification<T>

    fun or(specification: Specification<T>): Specification<T>

    fun not(specification: Specification<T>): Specification<T>
}