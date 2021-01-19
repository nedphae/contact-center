package com.qingzhu.common.util

import sun.misc.Unsafe

object UnsafeUtils {

    fun getUnsafe(): Unsafe {
        val clazz = Unsafe::class.java
        val theUnsafeField = clazz.getDeclaredField("theUnsafe")
        theUnsafeField.isAccessible = true
        return theUnsafeField.get(clazz) as Unsafe
    }

}