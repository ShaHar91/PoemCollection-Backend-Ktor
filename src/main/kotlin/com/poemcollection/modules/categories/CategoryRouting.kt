package com.poemcollection.modules.categories

import io.ktor.server.routing.*

fun Route.categoryRouting() {

    route("categories") {
        get {
//            categoryRoutes.getAllCategories(call)
        }

//        get("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.getCategoryById(call) }
//
//        authenticate("admin") {
//            post { categoryRoutes.postCategory(call) }
//
//            put("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.updateCategoryById(call) }
//
//            delete("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.deleteCategoryById(call) }
//        }
    }
}
