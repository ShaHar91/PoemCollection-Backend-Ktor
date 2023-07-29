package com.poemcollection.controllers.poems

import com.poemcollection.controllers.BaseControllerTest
import com.poemcollection.controllers.poems.PoemInstrumentation.givenAPoem
import com.poemcollection.controllers.poems.PoemInstrumentation.givenAPoemDetail
import com.poemcollection.controllers.poems.PoemInstrumentation.givenAValidInsertPoem
import com.poemcollection.controllers.poems.PoemInstrumentation.givenAValidUpdatePoem
import com.poemcollection.data.dto.requests.poem.PoemDto
import com.poemcollection.domain.interfaces.ICategoryDao
import com.poemcollection.domain.interfaces.IPoemDao
import com.poemcollection.domain.interfaces.IReviewDao
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.Ratings
import com.poemcollection.modules.poems.PoemController
import com.poemcollection.modules.poems.PoemControllerImpl
import com.poemcollection.statuspages.*
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.koin.dsl.module

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PoemControllerTest : BaseControllerTest() {

    private val categoryDao: ICategoryDao = mockk()
    private val userDao: IUserDao = mockk()
    private val poemDao: IPoemDao = mockk()
    private val reviewDao: IReviewDao = mockk()

    private val controller: PoemController by lazy { PoemControllerImpl() }

    init {
        startInjection(
            module {
                single { categoryDao }
                single { userDao }
                single { poemDao }
                single { reviewDao }
            }
        )
    }

    @BeforeEach
    override fun before() {
        super.before()
        clearMocks(categoryDao, userDao, poemDao, reviewDao)
    }

    @Test
    fun `when creating poem with correct information, we return valid poemDetailDto`() {
        val postPoem = givenAValidInsertPoem()
        val createdPoem = givenAPoemDetail()

        coEvery { categoryDao.getListOfExistingCategoryIds(any()) } returns listOf(1)
        coEvery { poemDao.insertPoem(any(), any()) } returns createdPoem

        runBlocking {
            val responsePoem = controller.postPoem(1, postPoem)

            assertThat(responsePoem.title).isEqualTo(createdPoem.title)
            assertThat(responsePoem.body).isEqualTo(createdPoem.body)
            assertThat(responsePoem.categories.map { it.id }).isEqualTo(createdPoem.categories.map { it.id })
            assertThat(responsePoem.writer?.id).isEqualTo(createdPoem.writer?.id)
        }
    }

    @Test
    fun `when creating poem where categories do not exist, we return error stating which categories are wrong`() {
        val postPoem = givenAValidInsertPoem()

        coEvery { categoryDao.getListOfExistingCategoryIds(any()) } returns emptyList()

        assertThrows<ErrorUnknownCategoryIdsForUpdate> {
            runBlocking { controller.postPoem(1, postPoem) }
        }
    }

    @Test
    fun `when creating poem and database returns error, we throw exception`() {
        val postPoem = givenAValidInsertPoem()

        coEvery { categoryDao.getListOfExistingCategoryIds(any()) } returns listOf(1)
        coEvery { poemDao.insertPoem(any(), any()) } returns null

        assertThrows<ErrorFailedCreate> {
            runBlocking { controller.postPoem(1, postPoem) }
        }
    }

    @Test
    fun `when requesting all poems, we return valid list`() {
        val createdPoem = givenAPoem()

        coEvery { poemDao.getPoems(any()) } returns listOf(createdPoem)

        runBlocking {
            val responsePoem = controller.getAllPoems(null)

            assertThat(responsePoem).hasSize(1)
            assertThat(responsePoem).allMatch { it is PoemDto }
        }
    }

    @Test
    fun `when requesting specific poem, we return valid poemDetailDto`() {
        val createdPoem = givenAPoemDetail()

        coEvery { poemDao.getPoem(any()) } returns createdPoem

        runBlocking {
            val responsePoem = controller.getPoemById(1)

            assertThat(responsePoem.title).isEqualTo(createdPoem.title)
            assertThat(responsePoem.body).isEqualTo(createdPoem.body)
            assertThat(responsePoem.categories.map { it.id }).isEqualTo(createdPoem.categories.map { it.id })
            assertThat(responsePoem.writer?.id).isEqualTo(createdPoem.writer?.id)
        }
    }

    @Test
    fun `when requesting specific poem which does not exist, we throw exception`() {
        coEvery { poemDao.getPoem(any()) } throws ErrorNotFound

        assertThrows<ErrorNotFound> {
            runBlocking { controller.getPoemById(1) }
        }
    }

    @Test
    fun `when updating specific poem, we return valid poemDetailDto`() {
        val updatedPoem = givenAValidUpdatePoem()
        val createdPoem = givenAPoemDetail()

        coEvery { poemDao.updatePoem(any(), any()) } returns createdPoem
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { poemDao.isUserWriter(any(), any()) } returns true
        coEvery { categoryDao.getListOfExistingCategoryIds(any()) } returns listOf(1)

        runBlocking {
            val responsePoem = controller.updatePoemById(1, 1, updatedPoem)

            assertThat(responsePoem.title).isEqualTo(createdPoem.title)
            assertThat(responsePoem.body).isEqualTo(createdPoem.body)
            assertThat(responsePoem.categories.map { it.id }).isEqualTo(createdPoem.categories.map { it.id })
            assertThat(responsePoem.writer?.id).isEqualTo(createdPoem.writer?.id)
        }
    }

    @Test
    fun `when updating specific poem and user is not admin or writer, we throw error`() {
        val updatedPoem = givenAValidUpdatePoem()

        coEvery { userDao.isUserRoleAdmin(any()) } returns false
        coEvery { poemDao.isUserWriter(any(), any()) } returns false

        assertThrows<ErrorUnauthorized> {
            runBlocking { controller.updatePoemById(1, 1, updatedPoem) }
        }
    }

    @Test
    fun `when updating specific poem and list of new categories contains wrong categoryId, we return error stating which categories are wrong`() {
        val postPoem = givenAValidInsertPoem()

        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { poemDao.isUserWriter(any(), any()) } returns true
        coEvery { categoryDao.getListOfExistingCategoryIds(any()) } returns emptyList()

        assertThrows<ErrorUnknownCategoryIdsForUpdate> {
            runBlocking { controller.updatePoemById(1, 1, postPoem) }
        }
    }

    @Test
    fun `when deleting poem, we return without error`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { poemDao.isUserWriter(any(), any()) } returns true
        coEvery { poemDao.deletePoem(any()) } returns true

        assertDoesNotThrow {
            runBlocking { controller.deletePoemById(1, 1) }
        }
    }

    @Test
    fun `when deleting poem and user is not admin and writer, we return with error`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns false
        coEvery { poemDao.isUserWriter(any(), any()) } returns false

        assertThrows<ErrorUnauthorized> {
            runBlocking { controller.deletePoemById(1, 1) }
        }
    }

    @Test
    fun `when deleting poem which does not exist, we throw exception`() {
        coEvery { userDao.isUserRoleAdmin(any()) } returns true
        coEvery { poemDao.isUserWriter(any(), any()) } returns true
        coEvery { poemDao.deletePoem(any()) } returns false

        assertThrows<ErrorFailedDelete> {
            runBlocking { controller.deletePoemById(1, 1) }
        }
    }

    @Test
    fun `when requesting ratings we return RatingsDto`() {
        coEvery { reviewDao.calculateRatings(any()) } returns Ratings()

        runBlocking {
            val ratings = controller.getRatingsForPoem(1)

            assertThat(ratings).isEqualTo(Ratings())
        }
    }
}