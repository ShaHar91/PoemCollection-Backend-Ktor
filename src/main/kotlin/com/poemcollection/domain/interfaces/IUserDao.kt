package com.poemcollection.domain.interfaces

import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.UserHashable
import com.poemcollection.security.security.hashing.SaltedHash

interface IUserDao {

    suspend fun getUser(id: Int): User?
    suspend fun getUserHashableById(id: Int): UserHashable?
    suspend fun getUserHashableByEmail(email: String): UserHashable?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser): User?

    suspend fun updateUser(id: Int, user: UpdateUser): User?
    suspend fun deleteUser(id: Int): Boolean
    suspend fun userUnique(email: String): Boolean
    suspend fun isUserRoleAdmin(userId: Int): Boolean
    suspend fun updateUserPassword(userId: Int, saltedHash: SaltedHash): User?
}