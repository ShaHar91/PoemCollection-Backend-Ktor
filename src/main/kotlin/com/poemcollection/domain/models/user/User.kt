package com.poemcollection.domain.models.user

import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.domain.models.interfaces.DateAble
import io.ktor.server.auth.*

data class User(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = "",
    val role: UserRoles = UserRoles.User,
    @Transient val password: String? = null,
) : DateAble, Principal

