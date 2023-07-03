package com.poemcollection.utils

import com.poemcollection.ParamConstants
import com.poemcollection.domain.models.user.User
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

val ApplicationCall.user get() = authentication.principal<User>()!!

suspend inline fun <reified T> ApplicationCall.receiveOrRespondWithError(): T {
    return try {
        runCatching { receiveNullable<T>() }.getOrNull() ?: run {
            // TODO: I think this happened when the incoming data could not be parsed!
            throw TBDException
        }
    } catch (e: Exception) {
        // TODO: Not sure when it triggered this, more investigation is needed!
        throw TBDException
    }
}

//suspend fun ApplicationCall.getUserId(): Int? = principal<JWTPrincipal>()?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.Unauthorized, ErrorCodes.ErrorUnauthorized.asResponse)
//    null
//}

fun ApplicationCall.getUserId(): Int = parameters[ParamConstants.USER_ID_KEY]?.toIntOrNull() ?: throw TBDException
fun ApplicationCall.getCategoryIdNullable(): Int? = parameters[ParamConstants.CATEGORY_ID_KEY]?.toIntOrNull()
fun ApplicationCall.getCategoryId(): Int = getCategoryIdNullable() ?: throw TBDException
fun ApplicationCall.getPoemId(): Int = parameters[ParamConstants.POEM_ID_KEY]?.toIntOrNull() ?: throw TBDException

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