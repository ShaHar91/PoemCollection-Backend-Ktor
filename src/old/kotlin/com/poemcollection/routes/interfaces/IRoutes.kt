package com.poemcollection.routes.interfaces

import io.ktor.server.application.*

interface IAuthRoutes {
    suspend fun authorizeUser(call: ApplicationCall)
}

interface IUserRoutes {
    suspend fun postUser(call: ApplicationCall)
    suspend fun getCurrentUser(call: ApplicationCall)
    suspend fun updateCurrentUser(call: ApplicationCall)
    suspend fun updateCurrentUserPassword(call: ApplicationCall)
    suspend fun deleteCurrentUser(call: ApplicationCall)
}

interface ICategoryRoutes {
    suspend fun postCategory(call: ApplicationCall)
    suspend fun getAllCategories(call: ApplicationCall)
    suspend fun getCategoryById(call: ApplicationCall)
    suspend fun updateCategoryById(call: ApplicationCall)
    suspend fun deleteCategoryById(call: ApplicationCall)
}

interface IPoemRoutes {
    suspend fun postPoem(call: ApplicationCall)
    suspend fun getAllPoems(call: ApplicationCall)
    suspend fun getPoemById(call: ApplicationCall)
    suspend fun updatePoemById(call: ApplicationCall)
    suspend fun deletePoemById(call: ApplicationCall)
    suspend fun getRatingsForPoem(call: ApplicationCall)
}

interface IReviewRoutes {
    suspend fun postReview(call: ApplicationCall)
    suspend fun getAllReviews(call: ApplicationCall)
    suspend fun getReviewById(call: ApplicationCall)
    suspend fun updateReview(call: ApplicationCall)
    suspend fun deleteReview(call: ApplicationCall)
}
