package com.qingzhu.common.util

import org.springframework.web.server.ServerWebInputException
import javax.validation.Validation
import javax.validation.Validator
import javax.validation.ValidatorFactory

object ValidationUtils {
    var factory: ValidatorFactory = Validation.buildDefaultValidatorFactory()
    var validator: Validator = factory.validator

    fun <T> validate(obj: T) {
        val violations = validator.validate(obj)
        if (violations.isNotEmpty()) {
            throw ServerWebInputException(violations.first().message)
        }
    }
}