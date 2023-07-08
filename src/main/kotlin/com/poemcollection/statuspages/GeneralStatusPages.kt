package com.poemcollection.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

data class InternalServerException(val body: String? = null) : ApiException("internal_error", "Internal error " + body.orEmpty(), HttpStatusCode.InternalServerError)

object InvalidCategoryException : ApiException("invalid_category", "Invalid category", HttpStatusCode.BadRequest)


fun StatusPagesConfig.generalStatusPages() {
    exception<ApiException> { call, cause ->
        call.respond(cause.statusCode, cause.toErrorResponse())
    }
    exception<UnknownError> { call, _ ->
        val cause = InternalServerException()
        call.respond(cause.statusCode, cause.toErrorResponse())
    }
    exception<ExposedSQLException> { call, realCause ->
        val cause = ApiException("error", realCause.localizedMessage, HttpStatusCode.InternalServerError)
        call.respond(cause.statusCode, cause.toErrorResponse())
    }
}

open class ApiException(val error: String, val error_description: String, val statusCode: HttpStatusCode) : Exception() {
    open var errors: ArrayList<String>? = null
}

data class ErrorResponse(
    val error: String,
    val error_description: String,
    val status: Int,
    val errors: ArrayList<String>? = null
)

fun ApiException.toErrorResponse() = ErrorResponse(error, error_description.trim(), statusCode.value, errors)