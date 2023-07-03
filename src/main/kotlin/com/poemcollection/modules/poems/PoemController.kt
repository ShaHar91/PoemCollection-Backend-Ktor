package com.poemcollection.modules.poems

import com.poemcollection.data.dto.requests.poem.InsertOrUpdatePoem
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.domain.models.poem.Poem
import com.poemcollection.domain.models.poem.PoemDetail
import com.poemcollection.modules.BaseController
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PoemControllerImpl : BaseController(), PoemController, KoinComponent {

    private val categoryDao by inject<ICategoryDao>()
    private val userDao by inject<IUserDao>()
    private val poemDao by inject<IPoemDao>()
    private val reviewDao by inject<IReviewDao>()

    override suspend fun postPoem(userId: Int, insertPoem: InsertOrUpdatePoem): PoemDetail = dbQuery {
        val categoryIds = categoryDao.getListOfExistingCategoryIds(insertPoem.categoryIds)
        if (categoryIds.count() != insertPoem.categoryIds.count()) {
            val nonExistingIds = insertPoem.categoryIds.filterNot { categoryIds.contains(it) }
            throw TBDException // with the "nonExistingIds" added to it!!
        }

        poemDao.insertPoem(insertPoem, userId) ?: throw TBDException
    }

    override suspend fun getAllPoems(categoryId: Int?): List<Poem> = dbQuery {
        poemDao.getPoems(categoryId)
    }

    override suspend fun getPoemById(poemId: Int): PoemDetail = dbQuery {
        poemDao.getPoem(poemId) ?: throw TBDException
    }

    override suspend fun updatePoemById(userId: Int, poemId: Int, updatePoem: InsertOrUpdatePoem): PoemDetail = dbQuery {
        val isUserAdmin = userDao.isUserRoleAdmin(userId)
        val isUserWriter = poemDao.isUserWriter(poemId, userId)

        if (!isUserWriter && !isUserAdmin) throw TBDException

        val categoryIds = categoryDao.getListOfExistingCategoryIds(updatePoem.categoryIds)
        if (categoryIds.count() != updatePoem.categoryIds.count()) {
            val nonExistingIds = updatePoem.categoryIds.filterNot { categoryIds.contains(it) }
            throw TBDException // with the "nonExistingIds" added to it!!
        }

        poemDao.updatePoem(poemId, updatePoem) ?: throw TBDException
    }

    override suspend fun deletePoemById(userId: Int, poemId: Int) {
        return dbQuery {
            val isUserAdmin = userDao.isUserRoleAdmin(userId)
            val isUserWriter = poemDao.isUserWriter(poemId, userId)

            if (!isUserWriter && !isUserAdmin) throw TBDException

            poemDao.deletePoem(poemId)
        }
    }

    override suspend fun getRatingsForPoem(poemId: Int): Ratings = dbQuery {
        reviewDao.calculateRatings(poemId)
    }
}

interface PoemController {
    suspend fun postPoem(userId: Int, insertPoem: InsertOrUpdatePoem): PoemDetail
    suspend fun getAllPoems(categoryId: Int?): List<Poem>
    suspend fun getPoemById(poemId: Int): PoemDetail
    suspend fun updatePoemById(userId: Int, poemId: Int, updatePoem: InsertOrUpdatePoem): PoemDetail
    suspend fun deletePoemById(userId: Int, poemId: Int)
    suspend fun getRatingsForPoem(poemId: Int): Ratings
}
