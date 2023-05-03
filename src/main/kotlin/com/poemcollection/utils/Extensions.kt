package com.poemcollection.utils

import com.poemcollection.data.responses.ErrorCodes
import com.poemcollection.routes.ParamConstants
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*

suspend inline fun <reified T> ApplicationCall.receiveOrRespondWithError(): T? {
    return try {
        receiveNullable<T>() ?: run {
            respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
            null
        }

    } catch (e: Exception) {
        respond(HttpStatusCode.InternalServerError, ErrorCodes.ErrorInvalidContentType.asResponse)
        null
    }
}

suspend fun ApplicationCall.getUserId(): Int? = principal<JWTPrincipal>()?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)?.toIntOrNull() ?: run {
    respond(HttpStatusCode.Unauthorized, ErrorCodes.ErrorUnauthorized.asResponse)
    null
}

suspend fun ApplicationCall.getCategoryId(): Int? = parameters[ParamConstants.CATEGORY_ID_KEY]?.toIntOrNull() ?: run {
    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
    null
}

suspend fun ApplicationCall.getPoemId(): Int? = parameters[ParamConstants.POEM_ID_KEY]?.toIntOrNull() ?: run {
    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
    null
}

suspend fun ApplicationCall.reviewId(): Int? = parameters[ParamConstants.REVIEW_ID_KEY]?.toIntOrNull() ?: run {
    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
    null
}