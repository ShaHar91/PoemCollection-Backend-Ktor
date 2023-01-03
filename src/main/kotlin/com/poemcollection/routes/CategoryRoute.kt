package com.poemcollection.routes

import com.poemcollection.data.models.InsertOrUpdateCategory
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRouting(
    categoryDao: ICategoryDao
) {


    route("/categories") {
        authenticate {
            post {
                val insertCategory = call.receiveNullable<InsertOrUpdateCategory>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                val newCategory = categoryDao.insertCategory(insertCategory)

                if (newCategory != null) {
                    call.respond(HttpStatusCode.Created, newCategory)
                } else {
                    call.respondText("Not created", status = HttpStatusCode.InternalServerError)
                }
            }
        }

        get {
            val categories = categoryDao.getCategories()

            call.respond(HttpStatusCode.OK, categories)
        }

        get("{$CATEGORY_ID_KEY}") {
            val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val category = categoryDao.getCategory(id)

            if (category != null) {
                call.respond(HttpStatusCode.OK, category)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        authenticate {
            put("{$CATEGORY_ID_KEY}") {
                val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val updateCategory = call.receive<InsertOrUpdateCategory>()

                val category = categoryDao.updateCategory(id, updateCategory)

                if (category != null) {
                    call.respond(HttpStatusCode.OK, category)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }
        }

        authenticate {
            delete("{$CATEGORY_ID_KEY}") {
                val id = call.parameters[CATEGORY_ID_KEY]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val success = categoryDao.deleteCategory(id)

                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }
        }
    }
}