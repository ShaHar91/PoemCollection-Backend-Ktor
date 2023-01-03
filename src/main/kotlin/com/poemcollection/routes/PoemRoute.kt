package com.poemcollection.routes

import com.poemcollection.data.models.InsertPoem
import com.poemcollection.data.models.UpdatePoem
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.routes.ParamConstants.CATEGORY_ID_KEY
import com.poemcollection.routes.ParamConstants.POEM_ID_KEY
import com.poemcollection.security.security.token.TokenClaim
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.poemRouting(
    poemDao: IPoemDao,
    reviewDao: IReviewDao
) {

    route("poems") {
        authenticate {
            post {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.getClaim(TokenClaim.TOKEN_CLAIM_USER_ID_KEY, String::class)

                val insertPoem = call.receiveNullable<InsertPoem>() ?: run {
                    call.respond(HttpStatusCode.BadRequest)
                    return@post
                }

                // TODO: `writerId` should be taken out of the `insertPoem` class since this can be a security risk!!!
                val newPoem = poemDao.insertPoem(insertPoem.copy(writerId = userId?.toIntOrNull() ?: -1))

                if (newPoem != null) {
                    call.respond(HttpStatusCode.Created, newPoem)
                } else {
                    call.respondText("Not created", status = HttpStatusCode.InternalServerError)
                }
            }
        }

        get {
            val categoryId = call.request.queryParameters[CATEGORY_ID_KEY]?.toIntOrNull()

            val poems = poemDao.getPoems(categoryId)
            call.respond(HttpStatusCode.OK, poems)
        }

        route("{$POEM_ID_KEY}") {

            get {
                val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val poem = poemDao.getPoem(id)

                //TODO: maybe get a couple of things in a collection so the app doesn't have to do 3 seperate calls?
                // e.g. { "poem": {}, "ratings" : {}, "ownReview": {}, "reviews": {}} ----> where reviews are limited to 3 or 5 reviews...

                if (poem != null) {
                    call.respond(HttpStatusCode.OK, poem)
                } else {
                    call.respondText("Not found", status = HttpStatusCode.NotFound)
                }
            }

            authenticate {
                put {
                    // TODO: should only be able to update when the writerId is the same as the authenticated user!! (Or is an admin)
                    val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@put call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                    val updatePoem = call.receive<UpdatePoem>()

                    val poem = poemDao.updatePoem(id, updatePoem)

                    if (poem != null) {
                        call.respond(HttpStatusCode.OK, poem)
                    } else {
                        call.respondText("Not created", status = HttpStatusCode.InternalServerError)
                    }
                }
            }

            authenticate {
                delete {
                    // TODO: should only be able to delete when the writerId is the same as the authenticated user!! (Or is an admin)
                    val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@delete call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                    val success = poemDao.deletePoem(id)

                    if (success) {
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respondText("Not found", status = HttpStatusCode.NotFound)
                    }
                }
            }

            get("ratings") {
                val id = call.parameters[POEM_ID_KEY]?.toIntOrNull() ?: return@get call.respondText("Missing id", status = HttpStatusCode.BadRequest)

                val ratings = reviewDao.calculateRatings(id)

                call.respond(HttpStatusCode.OK, ratings)
            }

            reviewRouting(reviewDao)
        }
    }
}