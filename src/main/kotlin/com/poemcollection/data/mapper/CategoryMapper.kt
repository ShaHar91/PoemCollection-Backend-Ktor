package com.poemcollection.data.mapper

import com.poemcollection.data.remote.incoming.InsertOrUpdateCategoryDto
import com.poemcollection.data.remote.outgoing.CategoryDto
import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.category.InsertOrUpdateCategory

fun Category.toCategoryDto() = CategoryDto(
    id,
    name,
    createdAt,
    updatedAt
)

fun InsertOrUpdateCategoryDto.toInsertOrUpdateCategory() = InsertOrUpdateCategory(
    name
)