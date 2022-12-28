package com.poemcollection.plugins

import com.poemcollection.models.InsertNewUser
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<InsertNewUser> { newUser ->
            when  {
                newUser.email.isBlank() -> ValidationResult.Invalid("Email cannot be empty.")
                !newUser.email.contains('@', true) -> ValidationResult.Invalid("Invalid email, should contain at least an @ symbol.")
                else -> ValidationResult.Valid
            }
        }
    }
}