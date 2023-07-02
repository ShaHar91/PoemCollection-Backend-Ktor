package com.poemcollection.data.database.instrumentation

import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory

object CategoryInstrumentation {

    fun givenAValidInsertCategoryBody() = InsertOrUpdateCategory("Love")

    fun givenAValidUpdateCategoryBody() = InsertOrUpdateCategory("Family")
}