package com.poemcollection.controllers.poems

import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.category.Category
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail
import com.poemcollection.domain.models.user.User

object PoemInstrumentation {

    fun givenAValidInsertPoem() = InsertOrUpdatePoem("This is a title", "This is a large body that might be quite big", listOf(1))

    fun givenAValidUpdatePoem() = InsertOrUpdatePoem("This is an updated title", "This is a large body that might be quite big after an update", listOf(2))

    fun givenAPoemDetail() = PoemDetail(1, "This is a title", "This is a large body that might be quite big", User(1), listOf(Category(1, "Hate")), "", "")

    fun givenAPoem() = Poem(1, "This is a title", User(1), "", "")
}