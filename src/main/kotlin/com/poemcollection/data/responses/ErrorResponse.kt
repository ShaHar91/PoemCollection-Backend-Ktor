package com.poemcollection.data.responses

//@kotlinx.serialization.Serializable
//sealed interface ErrorResponse {
//    val error: String
//    val error_description: String
//    val status: Int
//    val errors: ArrayList<String>? get() = null
//}
//
//@kotlinx.serialization.Serializable
//data class AuthenticationError private constructor(
//    override val error: String = "hlleo",
//    override val error_description: String = "jklm",
//    override val status: Int = 401,
//    override val errors: ArrayList<String>? = arrayListOf()
//) : ErrorResponse

sealed class ErrorResponse(
    val error: String,
    val error_description: String,
    val status: Int,
    val errors: ArrayList<String>? = null
)

object ErrorInternalServerIssue : ErrorResponse("internal_server", "Something went wrong", 500)
object ErrorInvalidParameters : ErrorResponse("invalid_parameters", "The parameters provided are invalid.", 400)
object ErrorInvalidContentType : ErrorResponse("invalid_content_type", "The contentType provided is not valid.", 999)
object ErrorUnauthorized : ErrorResponse("unauthorized_request", "Unauthorized request: no authentication given.", 401)
object ErrorInvalidGrant : ErrorResponse("invalid_grant", "invalid grant: user credentials are invalid.", 400)