package com.qingzhu.staffadmin.config

import com.qingzhu.staffadmin.staff.controller.ShuntHandler
import com.qingzhu.staffadmin.staff.controller.ShuntUIConfigHandler
import com.qingzhu.staffadmin.staff.controller.StaffHandler
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
        shuntUIConfigHandler: ShuntUIConfigHandler
    ): RouterFunction<ServerResponse> {
        return coRouter {
            accept(MediaType.APPLICATION_JSON).nest {
                "/staff".nest {
                    GET("/info", staffHandler::findStaffInfo)
                    GET("/receptionist", staffHandler::findStaffConfigByOrganizationIdAndStaffId)
                    GET("/bots", staffHandler::findAllEnabledBotStaff)
                    "/shunt".nest {
                        GET("/{code}", shuntHandler::findFirstByCode)
                    }
                }
                "/config".nest {
                    GET("/chat-ui/config", shuntUIConfigHandler::getUIConfigByShunt)
                }
            }
        }
    }
}