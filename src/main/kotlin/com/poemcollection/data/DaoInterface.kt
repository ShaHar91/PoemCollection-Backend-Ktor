package com.poemcollection.data

import com.poemcollection.models.InsertNewUser
import com.poemcollection.models.UpdateUser
import com.poemcollection.models.User

interface UserDao {

    suspend fun getUser(userId: Int): User?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser): User?

    suspend fun updateUser(userId: Int, user: UpdateUser): User?

}