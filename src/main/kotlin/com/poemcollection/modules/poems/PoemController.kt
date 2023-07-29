package com.poemcollection.modules.poems

import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.data.dto.requests.poem.PoemDetailDto
import com.poemcollection.data.dto.requests.poem.PoemDto
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.domain.models.poem.toDto
import com.poemcollection.modules.BaseController
import com.poemcollection.statuspages.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PoemControllerImpl : BaseController(), PoemController, KoinComponent {

    private val categoryDao by inject<ICategoryDao>()
    private val userDao by inject<IUserDao>()
    private val poemDao by inject<IPoemDao>()
    private val reviewDao by inject<IReviewDao>()

    override suspend fun postPoem(userId: Int, insertPoem: InsertOrUpdatePoem): PoemDetailDto = dbQuery {
        val categoryIds = categoryDao.getListOfExistingCategoryIds(insertPoem.categoryIds)

        // A poem can only be added when all the added categories exist
        if (categoryIds.count() != insertPoem.categoryIds.count()) {
            val nonExistingIds = insertPoem.categoryIds.filterNot { categoryIds.contains(it) }
            throw ErrorUnknownCategoryIdsForUpdate(nonExistingIds)
        }

        poemDao.insertPoem(insertPoem, userId)?.toDto() ?: throw ErrorFailedCreate
    }

    override suspend fun getAllPoems(categoryId: Int?): List<PoemDto> = dbQuery {
        poemDao.getPoems(categoryId).map { it.toDto() }
    }

    override suspend fun getPoemById(poemId: Int): PoemDetailDto = dbQuery {
        poemDao.getPoem(poemId)?.toDto() ?: throw ErrorNotFound
    }

    override suspend fun updatePoemById(userId: Int, poemId: Int, updatePoem: InsertOrUpdatePoem): PoemDetailDto = dbQuery {
        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) throw ErrorUnauthorized

        val categoryIds = categoryDao.getListOfExistingCategoryIds(updatePoem.categoryIds)
        if (categoryIds.count() != updatePoem.categoryIds.count()) {
            val nonExistingIds = updatePoem.categoryIds.filterNot { categoryIds.contains(it) }
            throw ErrorUnknownCategoryIdsForUpdate(nonExistingIds)
        }

        poemDao.updatePoem(poemId, updatePoem)?.toDto() ?: throw ErrorFailedUpdate
    }

    override suspend fun deletePoemById(userId: Int, poemId: Int) {
        return dbQuery {
            val isUserAdmin = userDao.isUserRoleAdmin(userId)
            val isUserWriter = poemDao.isUserWriter(poemId, userId)

            if (!isUserWriter && !isUserAdmin) throw ErrorUnauthorized

            val deleted = poemDao.deletePoem(poemId)
            if (!deleted) throw ErrorFailedDelete
        }
    }

    override suspend fun getRatingsForPoem(poemId: Int): Ratings = dbQuery {
        reviewDao.calculateRatings(poemId)
    }
}

interface PoemController {
    suspend fun postPoem(userId: Int, insertPoem: InsertOrUpdatePoem): PoemDetailDto
    suspend fun getAllPoems(categoryId: Int?): List<PoemDto>
    suspend fun getPoemById(poemId: Int): PoemDetailDto
    suspend fun updatePoemById(userId: Int, poemId: Int, updatePoem: InsertOrUpdatePoem): PoemDetailDto
    suspend fun deletePoemById(userId: Int, poemId: Int)
    suspend fun getRatingsForPoem(poemId: Int): Ratings
}
