package com.poemcollection.routes

import com.poemcollection.data.CategoryDao
import com.poemcollection.models.InsertOrUpdateCategory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.categoryRouting() {

    val categoryDao by inject<CategoryDao>()

    route("/categories") {
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

        get {
            val categories = categoryDao.getCategories()

            call.respond(HttpStatusCode.OK, categories)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val category = categoryDao.getCategory(id)

            if (category != null) {
                call.respond(HttpStatusCode.OK, category)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val updateCategory = call.receive<InsertOrUpdateCategory>()

            val category = categoryDao.updateCategory(id, updateCategory)

            if (category != null) {
                call.respond(HttpStatusCode.OK, category)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val success = categoryDao.deleteCategory(id)

            if (success) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}