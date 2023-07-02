package com.poemcollection.data.dto.requests.poem

data class InsertOrUpdatePoem(
    val title: String = "",
    val body: String = "",
    val categoryIds: List<Int> = emptyList()
)