package com.poemcollection.domain.models.category

import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.domain.models.interfaces.DateAble

data class Category(
    val id: Int = 0,
    val name: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble

fun Category.toDto() = CategoryDto(
    this.id,
    this.name,
    this.createdAt,
    this.updatedAt
)

fun List<Category>.toDto() = this.map { it.toDto() }