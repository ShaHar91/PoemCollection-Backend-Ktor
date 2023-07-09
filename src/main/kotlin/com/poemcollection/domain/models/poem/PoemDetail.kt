package com.poemcollection.domain.models.poem

import com.poemcollection.data.dto.requests.poem.PoemDetailDto
import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.category.toDto
import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.toDto

data class PoemDetail(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: User? = null,
    val categories: List<Category> = emptyList(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble

fun PoemDetail.toDto() = PoemDetailDto(
    this.id,
    this.title,
    this.body,
    this.writer?.toDto(),
    this.categories.map { it.toDto() },
    this.createdAt,
    this.updatedAt
)