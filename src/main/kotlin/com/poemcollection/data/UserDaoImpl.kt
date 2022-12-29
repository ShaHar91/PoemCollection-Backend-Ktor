package com.poemcollection.data

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.models.InsertNewUser
import com.poemcollection.models.UpdateUser
import com.poemcollection.models.User
import com.poemcollection.models.Users
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
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

    override suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.userId eq id }.map(::resultRowToUser).firstOrNull()
    }

    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().map(::resultRowToUser)
    }

    override suspend fun insertUser(user: InsertNewUser): User? = dbQuery {
        Users.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.map(::resultRowToUser)?.singleOrNull()
    }

    override suspend fun updateUser(id: Int, user: UpdateUser): User? = dbQuery {
        val result = Users.update({ Users.userId eq id }) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            Users.select { Users.userId eq id }.map(::resultRowToUser).firstOrNull()
        } else {
            null
        }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        val result = Users.deleteWhere { userId eq id }

        result == 1
    }
}