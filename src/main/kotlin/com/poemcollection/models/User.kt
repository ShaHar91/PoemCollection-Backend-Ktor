package com.poemcollection.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable

@Serializable
data class User(
    val userId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class InsertNewUser(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = ""
)

@Serializable
data class UpdateUser(
    val firstName: String = "",
    val lastName: String = ""
)

object Users : IntIdTable() {
    val firstName = varchar("firstName", 255).default("")
    val lastName = varchar("lastName", 255).default("")
    val email = varchar("email", 255).uniqueIndex()
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)
}