package com.poemcollection.routes

import com.poemcollection.data.models.InsertPoem
import com.poemcollection.data.models.UpdatePoem
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.poemRouting(
    poemDao: IPoemDao,
    reviewDao: IReviewDao
) {

    route("poems") {
        post {
            // TODO: writerId should not come from the body, but should be from the authenticated user!!
            val insertPoem = call.receiveNullable<InsertPoem>() ?: run {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }

            val newPoem = poemDao.insertPoem(insertPoem)

            if (newPoem != null) {
                call.respond(HttpStatusCode.Created, newPoem)
            } else {
                call.respondText("Not created", status = HttpStatusCode.InternalServerError)
            }
        }

        get {
            val categoryId = call.request.queryParameters["category_id"]?.toIntOrNull()

            val poems = poemDao.getPoems(categoryId)
            call.respond(HttpStatusCode.OK, poems)
        }

        route("{id}") {

            get {
                val id = call.parameters["id"]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val poem = poemDao.getPoem(id)

                //TODO: maybe get a couple of things in a collection so the app doesn't have to do 3 seperate calls?
                // e.g. { "poem": {}, "ratings" : {}, "reviews": {}} ----> where reviews are limited to 3 or 5 reviews...

                if (poem != null) {
                    call.respond(HttpStatusCode.OK, poem)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }

            put {
                // TODO: should only be able to update when the writerId is the same as the authenticated user!! (Or is an admin)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val updatePoem = call.receive<UpdatePoem>()

                val poem = poemDao.updatePoem(id, updatePoem)

                if (poem != null) {
                    call.respond(HttpStatusCode.OK, poem)
                } else {
                    call.respondText("Not created", status = HttpStatusCode.InternalServerError)
                }
            }

            delete {
                // TODO: should only be able to delete when the writerId is the same as the authenticated user!! (Or is an admin)
                val id = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val success = poemDao.deletePoem(id)

                if (success) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }

            get("ratings") {
                //TODO:
                // { "total": 20, "five_star" : 7, "four_star" : 2, "three_star" : 2, "two_star" : 1, "one_star" : 0 }
                // ((7 * 5) + (2*4) + (3*2) + (1*2) + (1*0)) / (7+2+2+1) ---> general review number!!!
            }

            reviewRouting(reviewDao)
        }
    }
}