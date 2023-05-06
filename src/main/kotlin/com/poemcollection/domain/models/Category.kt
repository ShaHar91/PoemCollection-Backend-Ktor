package com.poemcollection.domain.models

import com.poemcollection.domain.models.interfaces.DateAble

@kotlinx.serialization.Serializable
data class Category(
    val id: Int = 0,
    val name: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble