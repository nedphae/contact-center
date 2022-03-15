package com.qingzhu.common.domain.shared

class NotSpecification<T>(private val spec: Specification<T>) : AbstractSpecification<T>() {

    override fun isSatisfiedBy(t: T): Boolean {
        return !spec.isSatisfiedBy(t)
    }
}