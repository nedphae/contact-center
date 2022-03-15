package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.common.domain.shared.msg.dto.ChatUIContent
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import com.qingzhu.common.util.JsonUtils
import com.qingzhu.common.util.toJson
import org.junit.jupiter.api.Test

class TestJson {

    @Test
    fun testJson() {
        var text = ChatUIMessage(content = ChatUIContent("你好")).toJson()
        println(text)
        println(JsonUtils.fromJson<ChatUIMessage>(text))
        text = """{"type":"text","content":{"text":"你好"},"position":"right","_id":"tve9ziw0ny", "hasTime":false}"""
        println(text)
        println(JsonUtils.fromJson<ChatUIMessage>(text))
    }
}