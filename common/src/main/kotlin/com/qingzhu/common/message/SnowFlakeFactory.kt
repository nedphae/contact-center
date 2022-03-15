package com.qingzhu.common.message

private enum class SnowFlakeEnum(private val snowFlake: SnowFlake) {
    CHAT_MESSAGE_SNOWFLAKE(SnowFlake(0, 1)),
    CONVERSATION_SNOWFLAKE(SnowFlake(0, 2)),
    CUSTOMER_SNOWFLAKE(SnowFlake(0, 4)),
    OTHERS_SNOWFLAKE(SnowFlake(0, 0));

    fun getSnowFlake(): SnowFlake {
        return this.snowFlake
    }
}

fun getChatMessageSnowFlake() = SnowFlakeEnum.CHAT_MESSAGE_SNOWFLAKE.getSnowFlake()
fun getConversationSnowFlake() = SnowFlakeEnum.CONVERSATION_SNOWFLAKE.getSnowFlake()
fun getCustomerSnowFlake() = SnowFlakeEnum.CUSTOMER_SNOWFLAKE.getSnowFlake()
fun getOthersSnowFlake() = SnowFlakeEnum.OTHERS_SNOWFLAKE.getSnowFlake()
