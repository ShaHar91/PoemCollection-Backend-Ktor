package com.poemcollection.data.dto.requests.poem

import com.google.gson.annotations.SerializedName
import com.poemcollection.data.dto.requests.category.CategoryDto
import com.poemcollection.data.dto.requests.user.UserDto
import com.poemcollection.domain.models.interfaces.DateAble

data class PoemDetailDto(
    val id: Int = 0,
    val title: String = "",
    val body: String = "",
    val writer: UserDto? = null,
    val categories: List<CategoryDto> = emptyList(),
    @SerializedName("created_at")
    override val createdAt: String = "",
    @SerializedName("updated_at")
    override val updatedAt: String = ""
) : DateAble
