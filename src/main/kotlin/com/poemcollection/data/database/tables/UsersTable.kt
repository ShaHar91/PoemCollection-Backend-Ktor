package com.poemcollection.data.database.tables

import com.poemcollection.domain.models.user.User
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDateTime

object UsersTable : IntIdTable() {
    val firstName = varchar("firstName", 255).default("")
    val lastName = varchar("lastName", 255).default("")
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
    val role = enumeration<UserRoles>("role").default(UserRoles.User)
}

enum class UserRoles {
    User,
    Admin
}

fun ResultRow.toUser() = User(
    id = this[UsersTable.id].value,
    firstName = this[UsersTable.firstName],
    lastName = this[UsersTable.lastName],
    email = this[UsersTable.email],
    createdAt = this[UsersTable.createdAt],
    updatedAt = this[UsersTable.updatedAt],
    role = this[UsersTable.role]
)

fun ResultRow.toUserHashable() = this.toUser().copy(password = this[UsersTable.password])

fun Iterable<ResultRow>.toUserHashable() = this.firstOrNull()?.toUserHashable()

fun Iterable<ResultRow>.toUsers() = this.map { it.toUser() }
fun Iterable<ResultRow>.toUser() = this.firstOrNull()?.toUser()
