package com.poemcollection.data.database

import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import java.time.LocalDateTime

object UsersTable : IntIdTable() {
    val firstName = varchar("firstName", 255).default("")
    val lastName = varchar("lastName", 255).default("")
    val email = varchar("email", 255).uniqueIndex()
    val password = varchar("password", 255)
    val salt = varchar("salt", 255)
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
    val role = enumeration<UserRoles>("role").default(UserRoles.User)
}

enum class UserRoles {
    User,
    Admin
}

object CategoriesTable : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

object PoemsTable : IntIdTable() {
    val title = varchar("title", 255)
    val body = mediumText("body")
    val writerId = reference("writerId", UsersTable) // --> see the link for more information https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

object PoemCategoryJunctionTable : IntIdTable() {
    val poemId = reference("poemId", PoemsTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Poems table this is enough.
    val categoryId = reference("categoryId", CategoriesTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Categories table this is enough.

    init {
        // Only a single pair can exist, duplicates are not allowed/necessary
        uniqueIndex(poemId, categoryId)
    }
}

object ReviewsTable : IntIdTable() {
    val body = mediumText("body")
    val rating = integer("rating").default(0)
    val userId = reference("userId", UsersTable, onDelete = ReferenceOption.SET_NULL) // As long as this foreign key references the primary key of the Users table this is enough.
    val poemId = reference("poemId", PoemsTable, onDelete = ReferenceOption.CASCADE) // As long as this foreign key references the primary key of the Poems table this is enough.
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}