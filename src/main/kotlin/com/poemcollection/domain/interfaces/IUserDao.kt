package com.poemcollection.domain.interfaces

import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdateUser
import com.poemcollection.domain.models.user.User

interface IUserDao {

    fun getUser(id: Int): User?
    fun getUserHashableById(id: Int): User?
    fun getUserHashableByEmail(email: String): User?
    fun getUsers(): List<User>
    fun insertUser(user: InsertNewUser): User?

    fun updateUser(id: Int, user: UpdateUser): User?
    fun deleteUser(id: Int): Boolean
    fun userUnique(email: String): Boolean
    fun isUserRoleAdmin(userId: Int): Boolean
    fun updateUserPassword(userId: Int, updatePassword: String): User?
}