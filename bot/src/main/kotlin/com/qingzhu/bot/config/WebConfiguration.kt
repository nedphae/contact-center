package com.qingzhu.bot.config

import com.qingzhu.bot.knowledgebase.controller.BotManageHandler
import com.qingzhu.bot.knowledgebase.controller.QABotHandler
import com.qingzhu.bot.knowledgebase.controller.TestUserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableWebFlux
class WebConfiguration : WebFluxConfigurer {
    @Bean
    fun routerFunction(
        authProvider: TestUserHandler,
        qaBotHandler: QABotHandler,
        botManageHandler: BotManageHandler,
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/users".nest {
                    GET("/test", authProvider::getStaff)
                    GET("/test/test", authProvider::getStaffTest)
                }
                "/bot".nest {
                    GET("/qa", qaBotHandler::getAnswer)
                    "/manage".nest {
                        POST("/topic", botManageHandler::saveTopic)
                        POST("/botConfig", botManageHandler::saveBotConfig)
                        POST("/knowledgeBase", botManageHandler::saveKnowledgeBase)
                        POST("/topicCategory", botManageHandler::saveTopicCategory)
                        // 查找
                        GET("/topic", botManageHandler::findAllTopic)
                        GET("/botConfig", botManageHandler::findAllBotConfig)
                        GET("/knowledgeBase", botManageHandler::findAllKnowledgeBase)
                        GET("/topicCategory", botManageHandler::findAllTopicCategory)
                        // 删除
                        PUT("/topic/deleteByIds", botManageHandler::deleteTopicByIds)
                        PUT("/botConfig/deleteByIds", botManageHandler::deleteBotConfigByIds)
                        PUT("/knowledgeBase/deleteByIds", botManageHandler::deleteKnowledgeBaseByIds)
                        PUT("/topicCategory/deleteByIds", botManageHandler::deleteTopicCategoryByIds)
                    }
                }
            }
        }
    }
}