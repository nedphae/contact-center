package com.qingzhu.dispatcher.config

import com.qingzhu.dispatcher.controller.AssignmentHandler
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
    fun routerFunction(assignmentHandler: AssignmentHandler,
                       customerHandler: CustomerHandler): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/customer".nest {
                    // 保存客户信息
                    PUT("", customerHandler::saveAndGetCustomer)
                }
                // 分配客服
                "/assignment".nest {
                    PUT("/staff", assignmentHandler::assignmentStaff)
                    // 检查是否已经分配过客服
                    // 如果是就重新分配到该客服，如果客服不在线就重新分配客服
                    // 如果没有就返回空
                    PUT("/auto", assignmentHandler::assignmentAuto)
                }
            }
        }
    }
}