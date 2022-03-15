package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.BotApplicationTests
import com.qingzhu.bot.knowledgebase.domain.entity.Answer
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.repository.search.TopicRepository
import com.qingzhu.common.domain.shared.msg.dto.ChatUIContent
import com.qingzhu.common.domain.shared.msg.dto.ChatUIMessage
import com.qingzhu.common.security.password.toMd5Hex
import com.qingzhu.common.util.toJson
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class BotManageServiceTest : BotApplicationTests() {
    @Autowired
    private lateinit var botManageService: BotManageService

    @Autowired
    private lateinit var qaBotService: QABotService

    @Autowired
    private lateinit var topicRepository: TopicRepository

    /**
     * 添加机器人测试文件
     */
    @Test
    fun initTest() {
        runBlocking {
            // var base = KnowledgeBase(9491, "机器人测试", "机器人的测试问题")
            // base = botManageService.saveKnowledgeBase(base)
            // println("知识库: $base")
            // var botConfig = BotConfig(9491, 2, base.id ?: -1)
            // botConfig = botManageService.saveBotConfig(botConfig)
            // println("机器人配置: $botConfig")
            // var topicCategory = TopicCategory(9491, "测试", null)
            // topicCategory = botManageService.saveTopicCategory(topicCategory)
            // println("知识类别: $topicCategory")
            val md5 = "你好".toMd5Hex()
            println("md5: $md5")
            var topic = Topic(
                9491, 1, "你好",
                md5, listOf(Answer("text", "你好，这里是 QA 机器人")), "欢迎语", 0,
                1, null, null, true,
                null, null, 1
            )
            topic = botManageService.saveTopic(topic)
            println("知识: $topic")
            val ans = qaBotService.findAnswerByQuestion(1, 2, 1, ChatUIMessage(content = ChatUIContent("你好")))
            println("答案: $ans")
        }
    }
    @Test
    fun testQuery() {
        runBlocking {
            var list = topicRepository.findByKnowledgeBaseIdAndQuestion(1, "你好").asFlow().toList()
            println(list.toJson())
            list = topicRepository.findByKnowledgeBaseIdAndQuestion(1, "你").asFlow().toList()
            println(list.toJson())
            list = topicRepository.findByKnowledgeBaseIdAndQuestion(1, "好").asFlow().toList()
            println(list.toJson())
        }
    }
}