package com.poemcollection.data.dto.requests.user

import com.google.gson.annotations.SerializedName
import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.domain.models.interfaces.DateAble

data class UserDto(
    val id: Int = 0,
    @SerializedName("first_name")
    override val firstName: String = "",
    @SerializedName("last_name")
    override val lastName: String = "",
    val email: String = "",
    @SerializedName("created_at")
    override val createdAt: String = "",
    @SerializedName("updated_at")
    override val updatedAt: String = "",
    val role: UserRoles = UserRoles.User
) : DateAble, NameAble