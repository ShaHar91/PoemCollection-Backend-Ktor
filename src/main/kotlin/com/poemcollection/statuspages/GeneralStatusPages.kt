package com.poemcollection.statuspages

import io.ktor.http.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

data class InternalServerException(val body: String? = null) : ApiException("internal_error", "Internal error " + body.orEmpty(), HttpStatusCode.InternalServerError)

object InvalidCategoryException : ApiException("invalid_category", "Invalid category", HttpStatusCode.BadRequest)


object ErrorDuplicateEntity : ApiException("duplicate_entity", "The entity already exists", HttpStatusCode.Conflict)
object ErrorEmailExists : ApiException("email_exists", "The email provided already exists", HttpStatusCode.Conflict)
object ErrorPasswordsDoNotMatch : ApiException("passwords_do_not_match", "The provided passwords do not match", HttpStatusCode.Conflict)
object ErrorSameAsOldPassword : ApiException("same_as_old_password", "The provided password cannot be the same as the old password", HttpStatusCode.Conflict)
object ErrorWeakPassword : ApiException("weak_password", "The provided password is too weak", HttpStatusCode.Conflict)
object ErrorFailedCreate : ApiException("create_failed", "The resource could not be created", HttpStatusCode.InternalServerError)
object ErrorFailedDelete : ApiException("delete_failed", "The resource could not be deleted", HttpStatusCode.InternalServerError)
object ErrorFailedUpdate : ApiException("update_failed", "The resource could not be updated", HttpStatusCode.Conflict)
object ErrorInvalidCredentials : ApiException("invalid_credentials", "The credentials provided are invalid", HttpStatusCode.Unauthorized)
object ErrorInvalidParameters : ApiException("invalid_parameters", "The parameters provided are invalid", HttpStatusCode.BadRequest)
data class ErrorUnknownCategoryIdsForUpdate(private val ids: List<Int>) :
    ApiException("unknown_ids_for_update", "Can't update poem with unknown categories ${ids.joinToString(", ")}", HttpStatusCode.BadRequest)

object ErrorInvalidRequest : ApiException("invalid_request", "Invalid request malformed authorization header", HttpStatusCode.BadRequest)
object ErrorInvalidScope : ApiException("invalid_scope", "Invalid scope Requested scope is invalid", HttpStatusCode.BadRequest)
object ErrorInvalidToken : ApiException("invalid_scope", "The token provided is not valid", HttpStatusCode.BadRequest)
object ErrorInvalidUUID : ApiException("invalid_uuid", "The uuid provided is not a valid uuid", HttpStatusCode.BadRequest)
object ErrorMissingParameters : ApiException("missing_parameters", "Missing parameters for required field", HttpStatusCode.BadRequest)
object ErrorNotFound : ApiException("not_found", "The resource could not be found", HttpStatusCode.NotFound)
object ErrorUnauthorized : ApiException("unauthorized", "The user is not authorized to perform this action", HttpStatusCode.Forbidden)

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