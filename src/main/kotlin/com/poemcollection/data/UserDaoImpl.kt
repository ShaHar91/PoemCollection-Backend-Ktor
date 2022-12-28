package com.poemcollection.data

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.models.InsertNewUser
import com.poemcollection.models.UpdateUser
import com.poemcollection.models.User
import com.poemcollection.models.Users
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class UserDaoImpl : UserDao {

    private fun resultRowToUser(row: ResultRow) = User(
        userId = row[Users.userId],
        firstName = row[Users.firstName],
        lastName = row[Users.lastName],
        email = row[Users.email],
        createdAt = row[Users.createdAt],
        updatedAt = row[Users.updatedAt]
    )

    override suspend fun getUser(userId: Int): User? = dbQuery {
        Users.select { Users.userId eq userId }.map(::resultRowToUser).firstOrNull()
    }

    override suspend fun getUsers(): List<User> {
        return dbQuery {
            Users.selectAll().map(::resultRowToUser)
        }
    }

    override suspend fun insertUser(user: InsertNewUser): User? {
        return dbQuery {
            Users.insert {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[email] = user.email
                it[createdAt] = LocalDateTime.now().toDatabaseString()
                it[updatedAt] = LocalDateTime.now().toDatabaseString()
            }.resultedValues?.map(::resultRowToUser)?.singleOrNull()
        }
    }

    override suspend fun updateUser(userId: Int, user: UpdateUser): User? {
        return dbQuery {
            val result = Users.update({ Users.userId eq userId}) {
                it[firstName] = user.firstName
                it[lastName] = user.lastName
                it[updatedAt] = LocalDateTime.now().toDatabaseString()
            }

            if (result == 1) {
                Users.select { Users.userId eq userId }.map(::resultRowToUser).firstOrNull()
            } else {
                null
            }
        }
    }
}