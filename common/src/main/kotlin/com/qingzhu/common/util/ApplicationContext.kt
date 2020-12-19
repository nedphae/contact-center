package com.qingzhu.common.util

import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

object ApplicationContextManager {
    lateinit var applicationContext: ApplicationContext
}

fun getApplicationContext(): ApplicationContext = ApplicationContextManager.applicationContext

fun <T : Any> KClass<T>.getBeanLazy(): Lazy<T> = lazy { getApplicationContext().getBean(this.java) }

fun <T : Any> KClass<T>.getBean(): T = getApplicationContext().getBean(this.java)