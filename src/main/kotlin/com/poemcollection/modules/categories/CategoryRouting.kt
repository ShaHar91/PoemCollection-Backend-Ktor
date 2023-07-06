package com.poemcollection.modules.categories

import com.poemcollection.ParamConstants
import com.poemcollection.data.dto.requests.category.InsertOrUpdateCategory
import com.poemcollection.utils.getCategoryId
import com.poemcollection.utils.sendOk
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoryRouting() {

    val categoryController by inject<CategoryController>()

    route("categories") {
        get {
            val categories = categoryController.getAllCategories()
            call.respond(categories)
        }

        get("{${ParamConstants.CATEGORY_ID_KEY}}") {
            val categoryId = call.getCategoryId()
            val category = categoryController.getCategoryById(categoryId)
            call.respond(category)
        }

        authenticate("admin") {
            post {
                val insertCategory = call.receive<InsertOrUpdateCategory>()
                val category = categoryController.postCategory(insertCategory)
                call.respond(category)
            }

            put("{${ParamConstants.CATEGORY_ID_KEY}}") {
                val categoryId = call.getCategoryId()
                val updateCategory = call.receive<InsertOrUpdateCategory>()
                val category = categoryController.updateCategoryById(categoryId, updateCategory)
                call.respond(category)
            }

            delete("{${ParamConstants.CATEGORY_ID_KEY}}") {
                val categoryId = call.getCategoryId()
                categoryController.deleteCategoryById(categoryId)
                sendOk()
            }
        }
    }
}
