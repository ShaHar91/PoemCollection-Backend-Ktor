package com.poemcollection.utils

import com.poemcollection.ParamConstants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

//suspend inline fun <reified T> ApplicationCall.receiveOrRespondWithError(): T? {
//    return try {
//        runCatching { receiveNullable<T>() }.getOrNull() ?: run { // TODO: not very sure about this, but it will do for now
//            respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//            null
//        }
//
//    } catch (e: Exception) {
//        respond(HttpStatusCode.InternalServerError, ErrorCodes.ErrorInvalidContentType.asResponse)
//        null
//    }
//}
//
//suspend fun ApplicationCall.getUserId(): Int? = principal<JWTPrincipal>()?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.Unauthorized, ErrorCodes.ErrorUnauthorized.asResponse)
//    null
//}

fun ApplicationCall.getCategoryId(): Int = parameters[ParamConstants.CATEGORY_ID_KEY]?.toIntOrNull() ?: throw TBDException

//suspend fun ApplicationCall.getPoemId(): Int? = parameters[ParamConstants.POEM_ID_KEY]?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//    null
//}
//
//suspend fun ApplicationCall.reviewId(): Int? = parameters[ParamConstants.REVIEW_ID_KEY]?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//    null
//}

suspend fun PipelineContext<Unit, ApplicationCall>.sendOk() {
    call.respond(HttpStatusCode.OK)
}

object TBDException : Exception()