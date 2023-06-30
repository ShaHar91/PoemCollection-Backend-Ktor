package com.poemcollection.data.database.instrumentation

import com.poemcollection.domain.models.SaltedHash
import com.poemcollection.domain.models.category.InsertOrUpdateCategory
import com.poemcollection.domain.models.poem.InsertOrUpdatePoem
import com.poemcollection.domain.models.user.InsertNewUser

object PoemInstrumentation {

    fun givenAValidInsertCategoryBody() = InsertOrUpdateCategory("Love")

    fun givenASecondValidInsertCategoryBody() = InsertOrUpdateCategory("Hate")

    fun givenAValidInsertWriterBody() = InsertNewUser("christiano", "bolla", "christiano@example", SaltedHash("hash", "salt"))

    fun givenAValidInsertPoemBody() = InsertOrUpdatePoem("This is title", "This is body", listOf(1))

    fun givenAValidUpdatePoemBody() = InsertOrUpdatePoem("This is an updated title", "This is an updated body", listOf(1, 2))

}