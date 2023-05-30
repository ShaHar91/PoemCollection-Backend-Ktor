package com.poemcollection.data.remote.incoming.poem

@kotlinx.serialization.Serializable
data class InsertOrUpdatePoemDto(
    val title: String = "",
    val body: String = "",
    val categoryIds: List<Int> = emptyList()
)
