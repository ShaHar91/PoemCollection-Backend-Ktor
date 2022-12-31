package com.poemcollection.data

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val firstName = varchar("firstName", 255).default("")
    val lastName = varchar("lastName", 255).default("")
    val email = varchar("email", 255).uniqueIndex()
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)
}

object Categories : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)
}

object Poems : IntIdTable() {
    val title = varchar("title", 255)
    val body = mediumText("body")
    val writerId = reference("writerId", Users) // --> see the link for more information https://www.baeldung.com/kotlin/exposed-persistence#3-foreign-keys
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)
}

object PoemCategoryJunction : IntIdTable() {
    val poemId = reference("poemId", Poems)
    val categoryId = reference("referenceId", Categories)
}