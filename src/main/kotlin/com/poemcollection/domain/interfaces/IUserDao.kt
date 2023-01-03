package com.poemcollection.domain.interfaces

import com.poemcollection.data.models.InsertNewUser
import com.poemcollection.data.models.UpdateUser
import com.poemcollection.data.models.User

interface IUserDao {

    suspend fun getUser(id: Int): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser): User?

    suspend fun updateUser(id: Int, user: UpdateUser): User?
    suspend fun deleteUser(id: Int): Boolean
}