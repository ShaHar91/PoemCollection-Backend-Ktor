package com.poemcollection.data.remote.outgoing

import com.poemcollection.domain.models.interfaces.DateAble
import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class PoemDto(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: UserDto = UserDto(),
    val categories: List<CategoryDto> = emptyList(),
    @SerialName("created_at")
    override val createdAt: String = "",
    @SerialName("updated_at")
    override val updatedAt: String = ""
) : DateAble
