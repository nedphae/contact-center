package com.qingzhu.staffadmin.config

import com.qingzhu.staffadmin.staff.controller.*
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
        staffHandler: StaffHandler,
        shuntHandler: ShuntHandler,
        shuntUIConfigHandler: ShuntUIConfigHandler,
        quickReplyHandler: QuickReplyHandler,
        staffGroupHandler: StaffGroupHandler,
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/staff".nest {
                    GET("/info", staffHandler::findStaffInfo)
                    GET("/receptionist", staffHandler::findStaffConfigByOrganizationIdAndStaffId)
                    GET("/bots", staffHandler::findAllEnabledBotStaff)
                    GET("/all", staffHandler::findAllStaff)
                    PUT("/deleteByIds", staffHandler::deleteStaffByIds)
                    "/config".nest {
                        GET("/{id}", staffHandler::findStaffConfigByShuntId)
                        POST("", staffHandler::saveStaffConfig)
                    }
                    "/shunt".nest {
                        GET("/{code}", shuntHandler::findFirstByCode)
                        GET("/id/{id}", shuntHandler::findById)
                        GET("", shuntHandler::findAllShunt)
                        POST("", shuntHandler::saveShunt)
                        GET("/class/all", shuntHandler::findAllShuntClass)
                        POST("/class", shuntHandler::saveShuntClass)
                        PUT("/deleteByIds", shuntHandler::deleteAllByIds)
                    }
                    "/group".nest {
                        GET("/all", staffGroupHandler::findAllGroup)
                        POST("", staffGroupHandler::saveGroup)
                        PUT("/deleteByIds", staffGroupHandler::deleteAllByIds)
                    }
                    "/quick-reply".nest {
                        GET("/personal", quickReplyHandler::findQuickReplyByStaff)
                        GET("/all", quickReplyHandler::findQuickReplyByOrganizationId)
                        POST("", quickReplyHandler::saveQuickReply)
                        POST("/group", quickReplyHandler::saveQuickReplyGroup)
                        DELETE("/{id}", quickReplyHandler::deleteQuickReply)
                        DELETE("/group/{id}", quickReplyHandler::deleteQuickReplyGroup)
                    }
                }
                "/config".nest {
                    GET("/chat-ui/config", shuntUIConfigHandler::getUIConfigByShunt)
                    POST("/chat-ui/config", shuntUIConfigHandler::saveUIConfig)
                }
            }
        }
    }
}