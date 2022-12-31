package com.poemcollection.routes

import com.poemcollection.data.models.InsertPoem
import com.poemcollection.domain.interfaces.IPoemDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.poemRouting(
    poemDao: IPoemDao
) {

    route("/poems") {
        post {
            val insertPoem = call.receiveNullable<InsertPoem>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val newPoem = poemDao.insertPoem(insertPoem)

            if (newPoem != null) {
                val poem = poemDao.getPoem(newPoem)

                if (poem != null) {
                    call.respond(HttpStatusCode.Created, poem)
                } else {
                    call.respondText("Not created", status = HttpStatusCode.InternalServerError)
                }
            } else {
                call.respondText("Not created", status = HttpStatusCode.InternalServerError)
            }
        }

        get {
            val categoryId = call.request.queryParameters["category_id"]?.toIntOrNull()

            val poems = poemDao.getPoems(categoryId)
            call.respond(HttpStatusCode.OK, poems)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

            val poem = poemDao.getPoem(id)

            if (poem != null) {
                call.respond(HttpStatusCode.OK, poem)
            } else {
                call.respondText("Not found", status = HttpStatusCode.NotFound)
            }
        }

        get("{id}/ratings") {
            //TODO:
            // { "total": 20, "five_star" : 7, "four_star" : 2, "three_star" : 2, "two_star" : 1, "one_star" : 0 }
            // ((7 * 5) + (2*4) + (3*2) + (1*2) + (1*0)) / (7+2+2+1) ---> general review number!!!
        }

        put("{id}") { }

        delete("{id}") { }
    }

}