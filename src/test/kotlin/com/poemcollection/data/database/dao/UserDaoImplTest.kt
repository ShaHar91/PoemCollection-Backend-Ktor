package com.poemcollection.data.database.dao

import com.poemcollection.data.database.instrumentation.UserInstrumentation.givenAValidInsertUserBody
import com.poemcollection.data.database.instrumentation.UserInstrumentation.givenAValidUpdateUserBody
import com.poemcollection.data.database.instrumentation.UserInstrumentation.givenAValidUpdateUserPasswordBody
import com.poemcollection.data.database.instrumentation.UserInstrumentation.givenAnEmptyUpdateUserBody
import com.poemcollection.data.database.tables.UserRoles
import com.poemcollection.data.database.tables.UsersTable
import kotlinx.coroutines.delay
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

internal class UserDaoImplTest : BaseDaoTest() {

    private val dao = UserDaoImpl()

    @Test
    fun `getUser where item exists, return correct user`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            val userId = dao.insertUser(validUser)?.id
            val user = dao.getUser(userId!!)

            assertThat(user).matches {
                it?.email == validUser.email && it.firstName == validUser.firstName && it.lastName == validUser.lastName && it.role == UserRoles.User
            }
        }
    }

    @Test
    fun `getUser where item does not exist, return 'null'`() {
        withTables(UsersTable) {
            val user = dao.getUser(803)

            assertNull(user)
        }
    }

    @Test
    fun `insertUser where information is correct, database is storing user and returning correct content`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            val user = dao.insertUser(validUser)

            assertThat(user).matches {
                it?.email == validUser.email &&
                        it.lastName == validUser.lastName &&
                        it.firstName == validUser.firstName &&
                        it.role == UserRoles.User &&
                        it.id == 1 &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `insertUser where the same data exists, database will give error`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            dao.insertUser(validUser)

            assertThrows<ExposedSQLException> {
                dao.insertUser(givenAValidInsertUserBody())
            }
        }
    }

    @Test
    fun `updateUser where information is correct, database is storing information and returning the correct content`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            val userId = dao.insertUser(validUser)?.id

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(1000)

            val validUpdateUser = givenAValidUpdateUserBody()
            val user = dao.updateUser(userId!!, validUpdateUser)

            assertThat(user).matches {
                it?.email == validUpdateUser.email &&
                        it?.lastName == validUpdateUser.lastName &&
                        it?.firstName == validUpdateUser.firstName &&
                        it?.role == UserRoles.User &&
                        it.id == 1 &&
                        it.createdAt != it.updatedAt
            }
        }
    }

    @Test
    fun `updateUser where information is empty, database has not been changed`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            val userId = dao.insertUser(validUser)?.id

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(1000)

            val validUpdateUser = givenAnEmptyUpdateUserBody()
            val user = dao.updateUser(userId!!, validUpdateUser)

            assertThat(user).matches {
                it?.email == validUser.email &&
                        it.lastName == validUser.lastName &&
                        it.firstName == validUser.firstName &&
                        it.role == UserRoles.User &&
                        it.id == 1 &&
                        it.createdAt == it.updatedAt
            }
        }
    }

    @Test
    fun `updateUser where information is correct but user with id does not exist, database does nothing and returns 'null'`() {
        withTables(UsersTable) {
            val validUser = givenAValidUpdateUserBody()
            val user = dao.updateUser(203, validUser)

            assertNull(user)
        }
    }

    @Test
    fun `updateUserPassword where information is correct, database is storing information`() {
        withTables(UsersTable) {
            val validUser = givenAValidInsertUserBody()
            val userId = dao.insertUser(validUser)?.id

            // adding a delay so there is a clear difference between `updatedAt` and `createdAt`
            delay(1000)

            val validUpdateUserPassword = givenAValidUpdateUserPasswordBody()
            val user = dao.updateUserPassword(userId!!, validUpdateUserPassword.saltedHash)

            val hashedUser = dao.getUserHashableById(userId)

            assertThat(user).matches { it?.createdAt != it?.updatedAt }
            assertThat(hashedUser).matches {
                it?.email == validUser.email &&
                        it.password == validUpdateUserPassword.saltedHash.hash &&
                        it.salt == validUpdateUserPassword.saltedHash.salt
            }
        }
    }

    @Test
    fun `getUsers but none exist return empty list`() {
        withTables(UsersTable) {
            val list = dao.getUsers()
            assertThat(list).isEmpty()
        }
    }

    @Test
    fun `getUsers return the list`() {
        withTables(UsersTable) {
            dao.insertUser(givenAValidInsertUserBody())
            val list = dao.getUsers()
            assertThat(list).hasSize(1)
        }
    }

    @Test
    fun `deleteUser for id that exists, return true`() {
        withTables(UsersTable) {
            val id = dao.insertUser(givenAValidInsertUserBody())?.id
            val deleted = dao.deleteUser(id!!)
            assertTrue(deleted)
        }
    }

    @Test
    fun `deleteUser for id that does not exists, return false`() {
        withTables(UsersTable) {
            val deleted = dao.deleteUser(203)
            assertFalse(deleted)
        }
    }

    @Test
    fun `userUnique where user does not exist, return true`() {
        withTables(UsersTable) {
            val unique = dao.userUnique("hell@example")
            assertTrue(unique)
        }
    }

    @Test
    fun `userUnique where user does exist, return false`() {
        withTables(UsersTable) {
            dao.insertUser(givenAValidInsertUserBody())
            val unique = dao.userUnique("christiano@example")
            assertFalse(unique)
        }
    }

    @Test
    fun `isUserRoleAdmin where user does not exist, return false`() {
        withTables(UsersTable) {
            val isUserAdmin = dao.isUserRoleAdmin(839)
            assertFalse(isUserAdmin)
        }
    }

    @Test
    fun `isUserRoleAdmin where user does exist but is not admin, return false`() {
        withTables(UsersTable) {
            dao.insertUser(givenAValidInsertUserBody())
            val isUserAdmin = dao.isUserRoleAdmin(1)
            assertFalse(isUserAdmin)
        }
    }

    @Test
    fun `getUserHashableByEmail where user does not exist, return null`() {
        withTables(UsersTable) {
            val userHashable = dao.getUserHashableByEmail("hello@example.be")
            assertNull(userHashable)
        }
    }

    @Test
    fun `getUserHashableByEmail where user does exist, return ccorrect content`() {
        withTables(UsersTable) {
            val validInsertUser = givenAValidInsertUserBody()
            dao.insertUser(validInsertUser)
            val userHashable = dao.getUserHashableByEmail("christiano@example")

            assertThat(userHashable).matches {
                it?.email == validInsertUser.email &&
                        it.salt == userHashable?.salt &&
                        it.password == userHashable.password
            }
        }
    }
}