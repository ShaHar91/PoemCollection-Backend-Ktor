package com.poemcollection.data.mapper

import com.poemcollection.data.remote.incoming.poem.InsertOrUpdatePoemDto
import com.poemcollection.data.remote.outgoing.PoemDto
import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem

fun Poem.toPoemDto() = PoemDto(
    id,
    title,
    body,
    writer.toUserDto(),
    categories.map { it.toCategoryDto() },
    createdAt,
    updatedAt
)

fun InsertOrUpdatePoemDto.toInsertOrUpdatePoem() = InsertOrUpdatePoem(
    title,
    body,
    categoryIds
)