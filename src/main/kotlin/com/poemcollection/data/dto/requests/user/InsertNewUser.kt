package com.poemcollection.data.dto.requests.user

import com.google.gson.annotations.SerializedName

data class InsertNewUser(
    @SerializedName("first_name")
    override val firstName: String,
    @SerializedName("last_name")
    override val lastName: String,
    val email: String,
    override val password: String,
    @SerializedName("repeat_password")
    override val repeatPassword: String?
) : PasswordAble, NameAble {
    val isValid get() = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && repeatPassword?.isNotBlank() == true && email.isNotBlank()
}
