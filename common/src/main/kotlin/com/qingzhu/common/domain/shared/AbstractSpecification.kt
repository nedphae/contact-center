package com.qingzhu.common.domain.shared

abstract class AbstractSpecification<T> : Specification<T> {

    override fun and(specification: Specification<T>): Specification<T> {
        return AndSpecification(this, specification)
    }

    override fun or(specification: Specification<T>): Specification<T> {
        return OrSpecification(this, specification)
    }

    override fun not(specification: Specification<T>): Specification<T> {
        return NotSpecification(specification)
    }
}