package com.poemcollection.domain.models.user

import com.poemcollection.data.local.UserRoles
import com.poemcollection.domain.models.interfaces.DateAble

data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = "",
    val role: UserRoles = UserRoles.User
) : DateAble

