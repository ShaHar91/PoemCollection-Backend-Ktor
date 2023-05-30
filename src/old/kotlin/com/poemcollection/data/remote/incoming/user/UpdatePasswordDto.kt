package com.poemcollection.data.remote.incoming.user

import com.poemcollection.data.remote.incoming.PasswordAble
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class UpdatePasswordDto(
    @SerialName("old_password")
    val oldPassword: String,
    override val password: String,
    @SerialName("repeat_password")
    override val repeatPassword: String
) : PasswordAble
