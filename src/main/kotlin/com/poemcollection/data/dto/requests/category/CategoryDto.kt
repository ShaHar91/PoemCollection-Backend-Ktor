package com.poemcollection.data.dto.requests.category

import com.google.gson.annotations.SerializedName
import com.poemcollection.domain.models.interfaces.DateAble

data class CategoryDto(
    val id: Int = 0,
    val name: String = "",
    @SerializedName("created_at")
    override val createdAt: String = "",
    @SerializedName("updated_at")
    override val updatedAt: String = ""
) : DateAble