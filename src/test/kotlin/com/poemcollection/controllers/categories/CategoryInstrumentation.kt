package com.poemcollection.controllers.categories

import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.domain.models.category.Category
import com.poemcollection.utils.toDatabaseString
import java.time.LocalDateTime

object CategoryInstrumentation {

    fun givenAValidInsertCategory() = InsertOrUpdateCategory("Love")

    fun givenACategory() = run {
        val time = LocalDateTime.now().toDatabaseString()
        Category(1, "Love", time, time)
    }
}