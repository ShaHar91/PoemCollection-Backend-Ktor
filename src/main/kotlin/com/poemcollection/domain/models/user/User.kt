package com.poemcollection.domain.models.user

import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.data.dto.requests.user.NameAble
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.domain.models.interfaces.DateAble
import io.ktor.server.auth.*

data class User(
    val id: Int = 0,
    override val firstName: String = "",
    override val lastName: String = "",
    val email: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = "",
    val role: UserRoles = UserRoles.User,
    @Transient val password: String? = null,
) : DateAble, NameAble, Principal

fun User.toDto() = UserDto(
    this.id,
    this.firstName,
    this.lastName,
    this.email,
    this.createdAt,
    this.updatedAt,
    this.role
)