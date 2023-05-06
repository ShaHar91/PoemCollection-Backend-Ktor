package com.poemcollection.data.mapper

import com.poemcollection.data.remote.incoming.user.InsertNewUserDto
import com.poemcollection.data.remote.outgoing.UserDto
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.security.security.hashing.SaltedHash

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

fun InsertNewUserDto.toInsertNewUser(saltedHash: SaltedHash) = InsertNewUser(
    firstName,
    lastName,
    email,
    saltedHash
)
