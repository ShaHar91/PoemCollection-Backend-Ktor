package com.poemcollection.domain.models.poem

data class InsertOrUpdatePoem(
    val title: String = "",
    val body: String = "",
    val categoryIds: List<Int> = emptyList()
)