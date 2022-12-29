package com.poemcollection.plugins

import io.ktor.server.application.*

fun Application.configureValidation() {
//    install(RequestValidation) {
//        validate<InsertNewUser> { newUser ->
//            when  {
//                newUser.email.isBlank() -> ValidationResult.Invalid("Email cannot be empty.")
//                !newUser.email.contains('@', true) -> ValidationResult.Invalid("Invalid email, should contain at least an @ symbol.")
//                else -> ValidationResult.Valid
//            }
//        }
//    }
}