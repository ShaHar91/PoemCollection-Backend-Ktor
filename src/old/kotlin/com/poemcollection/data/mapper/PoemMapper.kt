package com.poemcollection.data.mapper

import com.poemcollection.data.remote.incoming.poem.InsertOrUpdatePoemDto
import com.poemcollection.data.remote.outgoing.PoemDetailDto
import com.poemcollection.data.remote.outgoing.PoemDto
import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail

fun PoemDetail.toPoemDetailDto() = PoemDetailDto(
    id,
    title,
    body,
    writer.toUserDto(),
    categories.map { it.toCategoryDto() },
    createdAt,
    updatedAt
)

fun Poem.toPoemDto() = PoemDto(
    id,
    title,
    writer.toUserDto(),
    createdAt,
    updatedAt
)

fun InsertOrUpdatePoemDto.toInsertOrUpdatePoem() = InsertOrUpdatePoem(
    title,
    body,
    categoryIds
)