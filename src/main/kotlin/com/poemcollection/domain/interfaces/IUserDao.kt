package com.poemcollection.domain.interfaces

import com.poemcollection.data.models.InsertNewUser
import com.poemcollection.data.models.UpdateUser
import com.poemcollection.data.models.User
import com.poemcollection.security.security.hashing.SaltedHash

interface IUserDao {

    suspend fun getUser(id: Int): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser, saltedHash: SaltedHash): User?

    suspend fun updateUser(id: Int, user: UpdateUser): User?
    suspend fun deleteUser(id: Int): Boolean
    suspend fun userUnique(email: String): Boolean
    suspend fun isUserRoleAdmin(userId: Int): Boolean
}