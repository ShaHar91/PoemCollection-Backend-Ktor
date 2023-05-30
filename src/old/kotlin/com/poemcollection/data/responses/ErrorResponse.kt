package com.poemcollection.data.responses

@kotlinx.serialization.Serializable
data class ErrorResponse(
    val error: String,
    val error_description: String,
    val errors: ArrayList<String>? = null
)

enum class ErrorCodes(private val code: String, private val message: String) {
    ErrorInternalServerIssue("internal_server", "Something went wrong"),
    ErrorInvalidParameters("invalid_parameters", "The parameters provided are invalid."),
    ErrorInvalidContentType("invalid_content_type", "The contentType provided is not valid."),
    ErrorUnauthorized("unauthorized_request", "Unauthorized request: no authentication given."),
    ErrorInvalidGrant("invalid_grant", "invalid grant: user credentials are invalid."),
    ErrorIncorrectPassword("incorrect_password", "Incorrect password."),
    ErrorInvalidCredentials("invalid_credentials", "Invalid email address or password."),
    ErrorInvalidPassword("short_password", "Your password should be at least 8 characters long and contain 1 capital letter."),
    ErrorInvalidNewPassword("invalid_new_password", "New password should not be same as your old password."),
    ErrorRepeatPassword("invalid_repeat_password", "Passwords do not match."),
    ErrorInvalidEmail("invalid_email", "Invalid email address"),
    ErrorInvalidPermissionsToUpdatePoem("invalid_permissions_poem_update", "You don't have the right permissions to update this poem."),
    ErrorInvalidPermissionsToDeletePoem("invalid_permissions_poem_delete", "You don't have the right permissions to delete this poem."),
    ErrorInvalidPermissionsToUpdateReview("invalid_permissions_review_update", "You don't have the right permissions to update this review."),
    ErrorInvalidPermissionsToDeleteReview("invalid_permissions_review_delete", "You don't have the right permissions to delete this review."),
    ErrorResourceNotFound("resource_not_found", "Resource not found."),
    ErrorValidation("validation_error", "... field should not be empty."),
    ErrorInvalidBody("invalid_content", "Invalid content"),


    ;

    val asResponse get() = ErrorResponse(code, message)
}

