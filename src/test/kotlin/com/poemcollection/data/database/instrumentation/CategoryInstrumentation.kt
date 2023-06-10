package com.poemcollection.data.database.instrumentation

import com.poemcollection.domain.models.category.InsertOrUpdateCategory

object CategoryInstrumentation {

    fun givenAValidInsertCategoryBody() = InsertOrUpdateCategory("Love")

    fun givenAValidUpdateCategoryBody() = InsertOrUpdateCategory("Family")
}