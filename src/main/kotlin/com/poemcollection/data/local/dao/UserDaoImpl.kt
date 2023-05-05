package com.poemcollection.data.local.dao

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.UserRoles
import com.poemcollection.data.UsersTable
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.UserHashable
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserDaoImpl : IUserDao {

    override suspend fun getUser(id: Int): User? = dbQuery {
        UsersTable.select { UsersTable.id eq id }.toUser()
    }

    override suspend fun getUserHashableByEmail(email: String): UserHashable? = dbQuery {
        UsersTable.select { UsersTable.email eq email }.toUserHashable()
    }

    override suspend fun getUsers(): List<User> = dbQuery {
        UsersTable.selectAll().toUsers()
    }

    override suspend fun insertUser(user: InsertNewUser): User? = dbQuery {
        UsersTable.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = user.saltedHash.hash
            it[salt] = user.saltedHash.salt
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toUsers()?.singleOrNull()
    }

    override suspend fun updateUser(id: Int, user: UpdateUser): User? = dbQuery {
        val result = UsersTable.update({ UsersTable.id eq id }) {
            user.firstName?.let { first -> it[firstName] = first }
            user.lastName?.let { last -> it[lastName] = last }
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            UsersTable.select { UsersTable.id eq id }.toUser()
        } else {
            null
        }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        val result = UsersTable.deleteWhere { UsersTable.id eq id }

        result == 1
    }

    override suspend fun userUnique(email: String): Boolean = dbQuery {
        UsersTable.select { UsersTable.email eq email }.empty()
    }

    override suspend fun isUserRoleAdmin(userId: Int): Boolean = dbQuery {
        UsersTable.select { UsersTable.id eq userId }.first()[UsersTable.role] == UserRoles.Admin
    }
}