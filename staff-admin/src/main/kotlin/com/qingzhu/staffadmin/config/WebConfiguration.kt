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
        quickRecoveryHandler: QuickRecoveryHandler,
        staffGroupHandler: StaffGroupHandler,
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/staff".nest {
                    GET("/info", staffHandler::findStaffInfo)
                    GET("/receptionist", staffHandler::findStaffConfigByOrganizationIdAndStaffId)
                    GET("/bots", staffHandler::findAllEnabledBotStaff)
                    GET("/all", staffHandler::findAllStaff)
                    "/shunt".nest {
                        GET("/{code}", shuntHandler::findFirstByCode)
                        GET("/id/{id}", shuntHandler::findById)
                        GET("/all", shuntHandler::findAllShunt)
                        POST("", shuntHandler::saveShunt)
                        GET("/class/all", shuntHandler::findAllShuntClass)
                        POST("/class", shuntHandler::saveShuntClass)
                    }
                    "/group".nest {
                        GET("/all", staffGroupHandler::findAllGroup)
                        POST("", staffGroupHandler::saveGroup)
                    }
                    "/quick-reply".nest {
                        GET("/personal", quickRecoveryHandler::findQuickRecoveryByStaff)
                        GET("/all", quickRecoveryHandler::findQuickRecoveryByOrganizationId)
                        POST("", quickRecoveryHandler::saveQuickReply)
                        POST("/group", quickRecoveryHandler::saveQuickReplyGroup)
                        DELETE("/{id}", quickRecoveryHandler::deleteQuickReply)
                        DELETE("/group/{id}", quickRecoveryHandler::deleteQuickReplyGroup)
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