package com.qingzhu.imaccess.util

import java.security.SecureRandom

val random = SecureRandom()

fun getRandomInt() = random.nextInt(1000000000)