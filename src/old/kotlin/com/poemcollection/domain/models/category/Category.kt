package com.poemcollection.domain.models.category

import com.poemcollection.domain.models.interfaces.DateAble

data class Category(
    val id: Int = 0,
    val name: String = "",
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble