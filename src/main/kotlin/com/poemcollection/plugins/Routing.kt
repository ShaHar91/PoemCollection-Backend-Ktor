package com.poemcollection.plugins

import com.poemcollection.routes.ParamConstants
import com.poemcollection.routes.interfaces.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    routing {
        val authRoutes by inject<IAuthRoutes>()
        authRouting(authRoutes)

        route("api/v1/") {
            val userRoutes by inject<IUserRoutes>()
            userRouting(userRoutes)

            val categoryRoutes by inject<ICategoryRoutes>()
            categoryRouting(categoryRoutes)

            val poemRoutes by inject<IPoemRoutes>()
            poemRouting(poemRoutes)

            val reviewRoutes by inject<IReviewRoutes>()
            reviewRouting(reviewRoutes)
        }
    }
}

fun Route.authRouting(
    authRoutes: IAuthRoutes
) {

    post("oauth/token") {
        authRoutes.authorizeUser(call)
    }
}

fun Route.userRouting(
    userRoutes: IUserRoutes
) {

    route("users") {
        post("register") { userRoutes.postUser(call) }

        authenticate {
            get("me") { userRoutes.getCurrentUser(call) }

            put("me") { userRoutes.updateCurrentUser(call) }

            put("me/password") { userRoutes.updateCurrentUserPassword(call) }

            delete("me") { userRoutes.deleteCurrentUser(call) }
        }

        authenticate("admin") {
            //TODO: create getById, updateById, and deleteById call for admin only?
        }
    }
}

fun Route.categoryRouting(
    categoryRoutes: ICategoryRoutes
) {

    route("categories") {
        get { categoryRoutes.getAllCategories(call) }

        get("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.getCategoryById(call) }

        authenticate("admin") {
            post { categoryRoutes.postCategory(call) }

            put("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.updateCategoryById(call) }

            delete("{${ParamConstants.CATEGORY_ID_KEY}}") { categoryRoutes.deleteCategoryById(call) }
        }
    }
}

fun Route.poemRouting(
    poemRoutes: IPoemRoutes
) {

    route("poems") {
        authenticate {
            // Only a real user can create a poem
            post { poemRoutes.postPoem(call) }
        }

        get { poemRoutes.getAllPoems(call) }

        route("{${ParamConstants.POEM_ID_KEY}}") {

            get { poemRoutes.getPoemById(call) }

            authenticate {
                put { poemRoutes.updatePoemById(call) }

                delete { poemRoutes.deletePoemById(call) }
            }

            get("ratings") { poemRoutes.getRatingsForPoem(call) }
        }
    }
}

fun Route.reviewRouting(
    reviewRoutes: IReviewRoutes
) {

    route("poems/{${ParamConstants.POEM_ID_KEY}}/reviews") {
        authenticate {
            post { reviewRoutes.postReview(call) }
        }

        get { reviewRoutes.getAllReviews(call) }

        route("{${ParamConstants.REVIEW_ID_KEY}}") {

            get { reviewRoutes.getReviewById(call) }

            authenticate {
                put { reviewRoutes.updateReview(call) }

                delete { reviewRoutes.deleteReview(call) }
            }
        }
    }
}
