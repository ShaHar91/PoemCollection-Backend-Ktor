package com.poemcollection.models

import org.jetbrains.exposed.dao.id.IntIdTable

@kotlinx.serialization.Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@kotlinx.serialization.Serializable
data class InsertOrUpdateCategory(
    val name: String = ""
)

object Categories : IntIdTable() {
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)
}