package com.poemcollection.data.remote.incoming.user

import com.poemcollection.data.remote.incoming.PasswordAble
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsertNewUserDto(
    @SerialName("first_name")
    val firstName: String = "",
    @SerialName("last_name")
    val lastName: String = "",
    override val password: String = "",
    @SerialName("repeat_password")
    override val repeatPassword: String = "",
    val email: String = ""
) : PasswordAble {

    val isValid get() = firstName.isNotBlank() && lastName.isNotBlank() && password.isNotBlank() && repeatPassword.isNotBlank() && email.isNotBlank()
}
