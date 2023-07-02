package com.poemcollection.data.database.instrumentation

import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.data.dto.requests.user.InsertNewUser

object PoemInstrumentation {

    fun givenAValidInsertCategoryBody() = InsertOrUpdateCategory("Love")

    fun givenASecondValidInsertCategoryBody() = InsertOrUpdateCategory("Hate")

    fun givenAValidInsertWriterBody() = InsertNewUser("christiano", "bolla", "christiano@example", "hashedPassword", null)

    fun givenAValidInsertPoemBody() = InsertOrUpdatePoem("This is title", "This is body", listOf(1))

    fun givenAValidUpdatePoemBody() = InsertOrUpdatePoem("This is an updated title", "This is an updated body", listOf(1, 2))

}