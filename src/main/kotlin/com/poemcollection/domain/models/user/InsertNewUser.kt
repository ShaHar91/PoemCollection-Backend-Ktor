package com.poemcollection.domain.models.user

import com.google.gson.annotations.SerializedName

data class InsertNewUser(
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    override val password: String,
    @SerializedName("repeat_password")
    override val repeatPassword: String?
) : PasswordAble {
    val isValid get() = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && repeatPassword?.isNotBlank() == true && email.isNotBlank()
}

interface PasswordAble {

    val password: String
    val repeatPassword: String?

    val isPasswordSame get() = password == repeatPassword

    // Password should at least be 8 characters long AND should contain at least 1 capital letter
    val isPwTooShort get() = password.length >= 8 && password.contains(Regex("[A-Z]"))
}