package com.qingzhu.bot.config

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
    fun routerFunction(authProvider: TestUserHandler): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/users".nest {
                    GET("/test", authProvider::getStaff)
                }
                "/users".nest {
                    GET("/test/test", authProvider::getStaffTest)
                }
            }
        }
    }
}