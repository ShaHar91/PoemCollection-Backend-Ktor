package com.poemcollection.data.responses

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: String,
    val error_description: String
)

enum class ErrorCodes(private val code: String, private val message: String) {
    ErrorInvalidParameters("invalid_parameters", "The parameters provided are invalid."),
    UnauthorizedError("unauthorized_request", "Unauthorized request: no authentication given."),
    InvalidGrantError("invalid_grant", "invalid grant: user credentials are invalid."),
    InvalidCredentialsError("invalid_credentials", "Invalid email address or password."),
    UserAlreadyExistsError("unique_key", "The email provided already exists.")


    ;

    val asResponse get() = ErrorResponse(code, message)
}

