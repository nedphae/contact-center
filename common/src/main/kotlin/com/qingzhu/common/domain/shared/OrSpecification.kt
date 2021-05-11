package com.qingzhu.common.domain.shared

class OrSpecification<T>(
    private val spec1: Specification<T>,
    private val spec2: Specification<T>
) : AbstractSpecification<T>() {

    override fun isSatisfiedBy(t: T): Boolean {
        return spec1.isSatisfiedBy(t) || spec2.isSatisfiedBy(t)
    }
}