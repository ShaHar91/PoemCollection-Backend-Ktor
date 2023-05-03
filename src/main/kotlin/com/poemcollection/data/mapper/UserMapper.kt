package com.poemcollection.data.mapper

import com.poemcollection.data.remote.UserDto
import com.poemcollection.domain.models.User

fun UserDto.toUser() = User(
    id,
    firstName,
    lastName,
    email,
    createdAt,
    updatedAt
)

fun User.toUserDto() = UserDto(
    id,
    firstName,
    lastName,
    email,
    createdAt,
    updatedAt
)