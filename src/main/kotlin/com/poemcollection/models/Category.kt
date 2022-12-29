package com.poemcollection.models

import org.jetbrains.exposed.sql.Table

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

object Categories : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255).uniqueIndex().default("")
    val createdAt = varchar("createdAt", 255)
    val updatedAt = varchar("updatedAt", 255)

    override val primaryKey = PrimaryKey(id)
}