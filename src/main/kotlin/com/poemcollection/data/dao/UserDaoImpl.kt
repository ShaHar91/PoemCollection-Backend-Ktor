package com.poemcollection.data.dao

import com.poemcollection.data.DatabaseFactory.dbQuery
import com.poemcollection.data.UserRoles
import com.poemcollection.data.Users
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.InsertNewUser
import com.poemcollection.domain.models.UpdateUser
import com.poemcollection.domain.models.User
import com.poemcollection.security.security.hashing.SaltedHash
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserDaoImpl : IUserDao {

    override suspend fun getUser(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }.toUser()
    }

    override suspend fun getUserByEmail(email: String): User? = dbQuery {
        Users.select { Users.email eq email }.toUser()
    }

    override suspend fun getUsers(): List<User> = dbQuery {
        Users.selectAll().toUsers()
    }

    override suspend fun insertUser(user: InsertNewUser, saltedHash: SaltedHash): User? = dbQuery {
        Users.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = saltedHash.hash
            it[salt] = saltedHash.salt
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toUsers()?.singleOrNull()
    }

    override suspend fun updateUser(id: Int, user: UpdateUser): User? = dbQuery {
        val result = Users.update({ Users.id eq id }) {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        if (result == 1) {
            Users.select { Users.id eq id }.toUser()
        } else {
            null
        }
    }

    override suspend fun deleteUser(id: Int): Boolean = dbQuery {
        val result = Users.deleteWhere { Users.id eq id }

        result == 1
    }

    override suspend fun userUnique(email: String): Boolean = dbQuery {
        Users.select { Users.email eq email }.empty()
    }

    override suspend fun isUserRoleAdmin(userId: Int): Boolean = dbQuery {
        Users.select { Users.id eq userId }.first()[Users.role] == UserRoles.Admin
    }
}