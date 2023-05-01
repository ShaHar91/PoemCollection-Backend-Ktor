package com.poemcollection.domain.models

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