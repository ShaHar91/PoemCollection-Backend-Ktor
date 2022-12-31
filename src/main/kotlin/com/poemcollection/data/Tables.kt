package com.poemcollection.data

import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.dao.id.IntIdTable
import java.time.LocalDateTime

object Users : IntIdTable() {
    val firstName = varchar("firstName", 255).default("")
    val lastName = varchar("lastName", 255).default("")
    val email = varchar("email", 255).uniqueIndex()
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

object Categories : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

object Poems : IntIdTable() {
    val title = varchar("title", 255)
    val body = mediumText("body")
    val writerId = reference("writerId", Users) // --> see the link for more information https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}

object PoemCategoryJunction : IntIdTable() {
    val poemId = reference("poemId", Poems) // As long as this foreign key references the primary key of the Poems table this is enough.
    val categoryId = reference("referenceId", Categories) // As long as this foreign key references the primary key of the Categories table this is enough.

    init {
        // Only a single pair can exist, duplicates are not allowed/necessary
        uniqueIndex(poemId, categoryId)
    }
}

object Reviews : IntIdTable() {
    val body = mediumText("body")
    val rating = double("rating").default(0.0)
    val userId = reference("userId", Users) // As long as this foreign key references the primary key of the Users table this is enough.
    val poemId = reference("poemId", Poems) // As long as this foreign key references the primary key of the Poems table this is enough.
    val createdAt = varchar("createdAt", 255).default(LocalDateTime.now().toDatabaseString())
    val updatedAt = varchar("updatedAt", 255).default(LocalDateTime.now().toDatabaseString())
}