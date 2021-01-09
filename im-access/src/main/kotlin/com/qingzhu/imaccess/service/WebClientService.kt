package com.qingzhu.imaccess.service

import com.qingzhu.imaccess.domain.dto.*
import com.qingzhu.imaccess.domain.value.Message
import com.qingzhu.imaccess.domain.view.ConversationView
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

/**
 * 使用 webflux webclient 优化
 */
@Service
class DispatchingCenter(@Qualifier("innerWebClient") webClientBuilder: WebClient.Builder) {
    private val webClient = webClientBuilder.baseUrl("http://dispatching-center").build()

    fun updateCustomer(customerDto: Mono<CustomerDto>): Mono<CustomerDto> {
        return webClient
                .post()
                .uri("/customer")
                .body(customerDto)
                .retrieve()
                .bodyToMono()
    }

    fun assignmentAuto(organizationId: Int, userId: Long): Mono<ConversationView> {
        return webClient
                .put()
                .uri {
                    it.path("/assignment/auto")
                            .queryParam("organizationId", organizationId)
                            .queryParam("userId", userId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }

    fun assignmentStaff(@RequestParam("organizationId") organizationId: Int, @RequestParam("userId") userId: Long): Mono<ConversationView>{
        return webClient
                .put()
                .uri {
                    it.path("/assignment/staff")
                            .queryParam("organizationId", organizationId)
                            .queryParam("userId", userId)
                            .build()
                }
                .retrieve()
                .bodyToMono()
    }
}

@Service
@AuthorizedFeignClient(name = "message-server")
interface MessageService {
    @PostMapping(value = ["/message/send"])
    fun send(message: Message)

    @PostMapping(value = ["/register/customer"])
    fun registerCustomer(customerDto: CustomerStatusDto)

    @PutMapping(value = ["/unregister/customer"])
    fun unregisterCustomer(customerDto: CustomerBaseStatusDto)

    @PostMapping(value = ["/register/staff"])
    fun registerStaff(staffStatusDto: StaffStatusDto)

    @PutMapping(value = ["/unregister/staff"])
    fun unregisterStaff(staffChangeStatusDto: StaffChangeStatusDto)

    @GetMapping(value = ["/status/customer/find-by-uid"])
    fun findCustomerByUid(@RequestParam("organizationId") organizationId: Int, @RequestParam("uid") uid: String): CustomerBaseStatusDto?
}

@Service
@AuthorizedFeignClient(name = "staff-admin")
interface StaffAdminService {
    @GetMapping(value = ["/staff/receptionist"])
    fun getReceptionistGroup(@RequestParam("organizationId") organizationId: Int, @RequestParam("staffId") staffId: Long): ReceptionistGroupDto?
}