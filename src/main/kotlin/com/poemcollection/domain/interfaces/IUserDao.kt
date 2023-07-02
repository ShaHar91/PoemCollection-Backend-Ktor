package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.SaltedHash
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.UserHashable

interface IUserDao {

    fun getUser(id: Int): User?
    fun getUserHashableById(id: Int): UserHashable?
    fun getUserHashableByEmail(email: String): UserHashable?
    fun getUsers(): List<User>
    fun insertUser(user: InsertNewUser): User?

    fun updateUser(id: Int, user: UpdateUser): User?
    fun deleteUser(id: Int): Boolean
    fun userUnique(email: String): Boolean
    fun isUserRoleAdmin(userId: Int): Boolean
    fun updateUserPassword(userId: Int, saltedHash: SaltedHash): User?
}