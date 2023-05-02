package com.poemcollection.domain.models

@kotlinx.serialization.Serializable
data class Poem(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: User = User(),
    val categories: List<Category> = emptyList(),
    val createdAt: String = "",
    val updatedAt: String = ""
)

@kotlinx.serialization.Serializable
data class InsertPoem(
    val title: String = "",
    val body: String = "",
    val categoryIds: List<Int> = emptyList()
)

@kotlinx.serialization.Serializable
data class UpdatePoem(
    val title: String = "",
    val body: String = "",
    val categoryIds: List<Int> = emptyList()
)
