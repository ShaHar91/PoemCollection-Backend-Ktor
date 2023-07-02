package com.poemcollection.data.dto.requests.user

import com.google.gson.annotations.SerializedName

data class UpdateUser(
    @SerializedName("first_name")
    override val firstName: String? = null,
    @SerializedName("last_name")
    override val lastName: String? = null,
    val email: String? = null
) : NameAble

fun UpdateUser.hasData() = firstName != null || lastName != null || email != null