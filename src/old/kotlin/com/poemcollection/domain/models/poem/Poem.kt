package com.poemcollection.domain.models.poem

import com.poemcollection.domain.models.interfaces.DateAble
import com.poemcollection.domain.models.user.User

data class Poem(
    val id: Int = 0,
    val title: String = "",
    val writer: User = User(),
    override val createdAt: String = "",
    override val updatedAt: String = ""
) : DateAble