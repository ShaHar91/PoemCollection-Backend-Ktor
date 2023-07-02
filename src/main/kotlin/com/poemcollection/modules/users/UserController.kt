package com.poemcollection.modules.users

import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.modules.BaseController
import com.poemcollection.utils.PasswordManagerContract
import com.poemcollection.utils.TBDException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserControllerImpl : BaseController(), UserController, KoinComponent {

    private val userDao by inject<IUserDao>()
    private val passwordEncryption by inject<PasswordManagerContract>()

    override suspend fun postUser(insertNewUser: InsertNewUser): User = dbQuery {
        if (!insertNewUser.isValid) throw TBDException

        val userUnique = userDao.userUnique(insertNewUser.email)
        if (!userUnique || !insertNewUser.email.contains("@")) throw TBDException

        if (!insertNewUser.isPasswordSame) throw TBDException

        if (!insertNewUser.isPwTooShort) throw TBDException

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

    override suspend fun updateUserPasswordById(userId: Int, oldPassword: String, newPassword: String): User = dbQuery {
        //TODO:
        User()
    }

    override suspend fun deleteUserById(userId: Int) {
        return dbQuery { userDao.deleteUser(userId) }
    }
}

interface UserController {
    suspend fun postUser(insertNewUser: InsertNewUser): User
    suspend fun getUserById(userId: Int): User
    suspend fun deleteUserById(userId: Int, updateUser: UpdateUser): User
    suspend fun updateUserPasswordById(userId: Int, oldPassword: String, newPassword: String): User
    suspend fun deleteUserById(userId: Int)
}
