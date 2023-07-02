package com.poemcollection.domain.models.user

import com.google.gson.annotations.SerializedName

data class UpdatePassword(
    @SerializedName("old_password")
    val oldPassword: String,
    override val password: String,
    @SerializedName("repeat_password")
    override val repeatPassword: String?
) : PasswordAble
