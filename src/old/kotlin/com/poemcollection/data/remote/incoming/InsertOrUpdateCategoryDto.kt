package com.poemcollection.data.remote.incoming

@kotlinx.serialization.Serializable
data class InsertOrUpdateCategoryDto(
    val name: String = ""
)
