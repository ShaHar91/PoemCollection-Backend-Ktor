package com.poemcollection.data

import com.poemcollection.models.InsertNewUser
import com.poemcollection.models.UpdateUser
import com.poemcollection.models.User

interface UserDao {

    suspend fun getUser(id: Int): User?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser): User?

    suspend fun updateUser(id: Int, user: UpdateUser): User?
    suspend fun deleteUser(id: Int): Boolean
}