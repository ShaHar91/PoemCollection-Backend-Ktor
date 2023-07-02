package com.poemcollection.modules.users

import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdatePassword
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.modules.BaseController
import com.poemcollection.modules.auth.TokenProvider
import com.poemcollection.utils.PasswordManagerContract
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserControllerImpl : BaseController(), UserController, KoinComponent {

    private val userDao by inject<IUserDao>()
    private val passwordEncryption by inject<PasswordManagerContract>()
    private val tokenProvider by inject<TokenProvider>()

    override suspend fun postUser(insertNewUser: InsertNewUser): User = dbQuery {
        if (!insertNewUser.isValid) throw TBDException

        val userUnique = userDao.userUnique(insertNewUser.email)
        if (!userUnique || !insertNewUser.email.contains("@")) throw TBDException

        if (!insertNewUser.isPasswordSame) throw TBDException

        if (!insertNewUser.isPasswordStrong) throw TBDException

        val encryptedPassword = passwordEncryption.encryptPassword(insertNewUser.password)

        userDao.insertUser(insertNewUser.copy(password = encryptedPassword, repeatPassword = null)) ?: throw TBDException
    }

    override suspend fun getUserById(userId: Int): User = dbQuery {
        userDao.getUser(userId) ?: throw TBDException
    }

    override suspend fun deleteUserById(userId: Int, updateUser: UpdateUser): User = dbQuery {
        if (updateUser.firstName == null && updateUser.lastName == null) throw TBDException

        userDao.updateUser(userId, updateUser) ?: throw TBDException
    }

    override suspend fun updateUserPasswordById(userId: Int, updatePassword: UpdatePassword): User = dbQuery {
        val userHashable = userDao.getUserHashableById(userId) ?: throw TBDException

        if (listOf(updatePassword.password, updatePassword.repeatPassword).any { it == updatePassword.oldPassword }) throw TBDException

        val isValidPassword = passwordEncryption.validatePassword(updatePassword.oldPassword, userHashable.password ?: "")
        if (!isValidPassword) throw  TBDException

        if (!updatePassword.isPasswordSame) throw TBDException

        if (!updatePassword.isPasswordStrong) throw TBDException

        val encryptedPassword = passwordEncryption.encryptPassword(updatePassword.password)

        userDao.updateUserPassword(userId, encryptedPassword) ?: throw TBDException
    }

    override suspend fun deleteUserById(userId: Int) {
        return dbQuery { userDao.deleteUser(userId) }
    }
}

interface UserController {
    suspend fun postUser(insertNewUser: InsertNewUser): User
    suspend fun getUserById(userId: Int): User
    suspend fun deleteUserById(userId: Int, updateUser: UpdateUser): User
    suspend fun updateUserPasswordById(userId: Int, updatePassword: UpdatePassword): User
    suspend fun deleteUserById(userId: Int)
}
