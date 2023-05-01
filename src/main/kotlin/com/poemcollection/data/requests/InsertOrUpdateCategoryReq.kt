package com.poemcollection.data.requests

@kotlinx.serialization.Serializable
data class InsertOrUpdateCategoryReq(
    val name: String = ""
)