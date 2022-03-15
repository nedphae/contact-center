package com.qingzhu.common.util

import arrow.core.k
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import java.beans.FeatureDescriptor

fun Any.getNullPropertyNames(): Array<String> {
    val wrappedSource: BeanWrapper = BeanWrapperImpl(this)
    return wrappedSource.propertyDescriptors.asSequence().k()
        .map { obj: FeatureDescriptor -> obj.name }
        .filter { propertyName -> wrappedSource.getPropertyValue(propertyName) == null }
        .toList().toTypedArray()
}