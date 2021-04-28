package com.qingzhu.bot.knowledgebase.service

import com.qingzhu.bot.BotApplicationTests
import com.qingzhu.bot.knowledgebase.domain.entity.BotConfig
import com.qingzhu.bot.knowledgebase.domain.entity.KnowledgeBase
import com.qingzhu.bot.knowledgebase.domain.entity.Topic
import com.qingzhu.bot.knowledgebase.domain.entity.TopicCategory
import com.qingzhu.common.security.password.toMd5Hex
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class BotManageServiceTest : BotApplicationTests() {
    @Autowired
    private lateinit var botManageService: BotManageService
    @Autowired
    private lateinit var qaBotService: QABotService

    /**
     * 添加机器人测试文件
     */
    @Test
    fun initTest() {
        runBlocking {
            var base = KnowledgeBase(9491, "机器人测试", "机器人的测试问题")
            base = botManageService.saveKnowledgeBase(base)
            println("知识库: $base")
            var botConfig = BotConfig(9491, 1, base.id ?: -1)
            botConfig = botManageService.saveBotConfig(botConfig)
            println("机器人配置: $botConfig")
            var topicCategory = TopicCategory(9491, "测试", null)
            topicCategory = botManageService.saveTopicCategory(topicCategory)
            println("知识类别: $topicCategory")
            val md5 = "你好".toMd5Hex()
            println("md5: $md5")
            var topic = Topic(9491, 1, "你好",
                    md5, "你好，这里是 QA 机器人", null, 0,
                    1, null, null, true,
                    null, null, 1)
            topic = botManageService.saveTopic(topic)
            println("知识: $topic")
            val ans = qaBotService.findAnswerByQuestion(1, 1, "你好")
            println("答案: $ans")
        }
    }
}