package com.poemcollection.domain.models.poem

import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User

data class Poem(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: User = User(),
    val categories: List<Category> = emptyList(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble