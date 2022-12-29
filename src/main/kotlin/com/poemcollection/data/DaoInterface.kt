package com.poemcollection.data

import com.poemcollection.models.*

interface UserDao {

    suspend fun getUser(id: Int): User?
    suspend fun getUsers(): List<User>
    suspend fun insertUser(user: InsertNewUser): User?

    suspend fun updateUser(id: Int, user: UpdateUser): User?
    suspend fun deleteUser(id: Int): Boolean
}

interface CategoryDao {

    suspend fun getCategory(id: Int): Category?
    suspend fun getCategories(): List<Category>
    suspend fun insertCategory(category: InsertOrUpdateCategory): Category?

    suspend fun updateCategory(id: Int, category: InsertOrUpdateCategory): Category?
    suspend fun deleteCategory(id: Int): Boolean
}