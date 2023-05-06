package com.poemcollection.domain.models

import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User

@kotlinx.serialization.Serializable
data class Poem(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: User = User(),
    val categories: List<Category> = emptyList(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble

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
