package com.poemcollection.data.responses

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: String,
    val error_description: String,
    val errors: ArrayList<String>? = null
)

enum class ErrorCodes(private val code: String, private val message: String) {
    ErrorInvalidParameters("invalid_parameters", "The parameters provided are invalid."),
    ErrorInvalidContentType("invalid_content_type", "The contentType provided is not valid."),
    ErrorUnauthorized("unauthorized_request", "Unauthorized request: no authentication given."),
    ErrorInvalidGrant("invalid_grant", "invalid grant: user credentials are invalid."),
    ErrorInvalidCredentials("invalid_credentials", "Invalid email address or password."),
    ErrorInvalidPassword("short_password", "Your password should be at least 8 characters long and contain 1 capital letter."),
    ErrorRepeatPassword("invalid_repeat_password", "Passwords do not match."),
    ErrorInvalidEmail("invalid_email", "Invalid email address"),


    ;

    val asResponse get() = ErrorResponse(code, message)
}

