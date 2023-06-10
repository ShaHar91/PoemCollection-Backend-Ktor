package com.poemcollection.utils

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
//
//suspend fun ApplicationCall.getCategoryId(): Int? = parameters[ParamConstants.CATEGORY_ID_KEY]?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//    null
//}
//
//suspend fun ApplicationCall.getPoemId(): Int? = parameters[ParamConstants.POEM_ID_KEY]?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//    null
//}
//
//suspend fun ApplicationCall.reviewId(): Int? = parameters[ParamConstants.REVIEW_ID_KEY]?.toIntOrNull() ?: run {
//    respond(HttpStatusCode.BadRequest, ErrorCodes.ErrorInvalidParameters.asResponse)
//    null
//}