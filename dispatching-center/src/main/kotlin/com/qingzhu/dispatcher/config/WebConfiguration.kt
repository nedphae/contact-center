package com.qingzhu.dispatcher.config

import com.qingzhu.dispatcher.controller.AssignmentHandler
import com.qingzhu.dispatcher.customer.controller.BlacklistHandler
import com.qingzhu.dispatcher.customer.controller.CommentHandler
import com.qingzhu.dispatcher.customer.controller.CustomerHandler
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
        assignmentHandler: AssignmentHandler,
        customerHandler: CustomerHandler,
        commentHandler: CommentHandler,
        BlacklistHandler: BlacklistHandler,
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/customer".nest {
                    // 保存客户信息 机器填充
                    POST("", customerHandler::saveIfNotExist)
                    // 保存客户信息 手动保存
                    PUT("", customerHandler::saveAndGetCustomer)
                    GET("", customerHandler::getCustomerById)
                    POST("/q", customerHandler::searchCustomer)
                    PUT("/deleteByIds", customerHandler::deleteByIds)
                    "/comment".nest {
                        POST("/q", commentHandler::findComment)
                        POST("", commentHandler::saveComment)
                    }
                    "/blacklist".nest {
                        POST("", BlacklistHandler::saveBlacklist)
                        POST("/delete", BlacklistHandler::remove)
                        GET("/all", BlacklistHandler::getAllBlacklist)
                        GET("", BlacklistHandler::getBlacklistBy)
                    }
                }
                // 分配客服
                "/dispatcher/assignment".nest {
                    PUT("/staff", assignmentHandler::assignmentStaff)
                    // 检查是否已经分配过客服
                    // 如果是就重新分配到该客服，如果客服不在线就重新分配客服
                    // 如果没有就返回空
                    PUT("/auto", assignmentHandler::assignmentAuto)
                    // 分配列队中等待的客户
                    PUT("/queue", assignmentHandler::assignmentFromQueue)
                    // 分配列队中等待的客户
                    PUT("/queue/for-staff", assignmentHandler::assignmentFromQueueForStaff)
                }
            }
        }
    }
}