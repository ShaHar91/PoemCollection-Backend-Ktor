package com.poemcollection.domain.models.poem

import com.poemcollection.data.dto.requests.poem.PoemDto
import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.toDto

data class Poem(
    val id: Int = 0,
    val title: String = "",
    val writer: User = User(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble

fun Poem.toDto() = PoemDto(
    this.id,
    this.title,
    this.writer.toDto(),
    this.createdAt,
    this.updatedAt
)