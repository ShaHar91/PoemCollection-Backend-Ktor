package com.poemcollection.modules.categories

import com.poemcollection.modules.BaseController
import org.koin.core.component.KoinComponent

class CategoryControllerImpl : BaseController(), CategoryController, KoinComponent {

    override suspend fun getAllCategories(): Any {
        return Unit
    }
}

interface CategoryController {
    suspend fun getAllCategories(): Any
}