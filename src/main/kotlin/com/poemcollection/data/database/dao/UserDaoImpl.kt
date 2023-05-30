package com.poemcollection.data.database.dao

import com.poemcollection.data.database.UserRoles
import com.poemcollection.data.database.UsersTable
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.domain.models.SaltedHash
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.domain.models.user.UserHashable
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserDaoImpl : IUserDao {

    override suspend fun getUser(id: Int): User? =
        UsersTable.select { UsersTable.id eq id }.toUser()

    override suspend fun getUserHashableById(id: Int): UserHashable? =
        UsersTable.select { UsersTable.id eq id }.toUserHashable()

    override suspend fun getUserHashableByEmail(email: String): UserHashable? =
        UsersTable.select { UsersTable.email eq email }.toUserHashable()

    override suspend fun getUsers(): List<User> =
        UsersTable.selectAll().toUsers()

    override suspend fun insertUser(user: InsertNewUser): User? =
        UsersTable.insert {
            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = user.saltedHash.hash
            it[salt] = user.saltedHash.salt
            it[createdAt] = LocalDateTime.now().toDatabaseString()
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }.resultedValues?.toUsers()?.singleOrNull()

    override suspend fun updateUser(id: Int, user: UpdateUser): User? {
        UsersTable.update({ UsersTable.id eq id }) {
            user.firstName?.let { first -> it[firstName] = first }
            user.lastName?.let { last -> it[lastName] = last }
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return UsersTable.select { UsersTable.id eq id }.toUser()
    }

    override suspend fun deleteUser(id: Int): Boolean =
        UsersTable.deleteWhere { UsersTable.id eq id } > 0

    override suspend fun userUnique(email: String): Boolean =
        UsersTable.select { UsersTable.email eq email }.empty()

    override suspend fun isUserRoleAdmin(userId: Int): Boolean =
        UsersTable.select { UsersTable.id eq userId }.first()[UsersTable.role] == UserRoles.Admin

    override suspend fun updateUserPassword(userId: Int, saltedHash: SaltedHash): User? {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[password] = saltedHash.hash
            it[salt] = saltedHash.salt
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return UsersTable.select { UsersTable.id eq userId }.toUser()
    }
}