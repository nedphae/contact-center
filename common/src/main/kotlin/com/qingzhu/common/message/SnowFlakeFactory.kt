package com.qingzhu.common.message

private enum class SnowFlakeEnum(private val snowFlake: SnowFlake) {
    CHAT_MESSAGE_SNOWFLAKE(SnowFlake(0, 1)),
    USER_GROUP_SNOWFLAKE(SnowFlake(0, 2)),
    OTHERS_SNOWFLAKE(SnowFlake(0, 3));

    fun getSnowFlake(): SnowFlake {
        return this.snowFlake
    }
}

fun getChatMessageSnowFlake() = SnowFlakeEnum.CHAT_MESSAGE_SNOWFLAKE.getSnowFlake()
fun getUserGroupSnowFlake() = SnowFlakeEnum.USER_GROUP_SNOWFLAKE.getSnowFlake()
fun getOthersSnowFlake() = SnowFlakeEnum.OTHERS_SNOWFLAKE.getSnowFlake()
