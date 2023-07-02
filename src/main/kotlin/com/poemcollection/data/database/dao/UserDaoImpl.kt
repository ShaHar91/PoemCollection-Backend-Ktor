package com.poemcollection.data.database.dao

import com.poemcollection.data.database.tables.*
import com.poemcollection.domain.interfaces.IUserDao
import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdateUser
import com.poemcollection.domain.models.user.User
import com.poemcollection.data.dto.requests.user.hasData
import com.poemcollection.utils.toDatabaseString
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime

class UserDaoImpl : IUserDao {

    override fun getUser(id: Int): User? =
        UsersTable.select { UsersTable.id eq id }.toUser()

    override fun getUserHashableById(id: Int): User? =
        UsersTable.select { UsersTable.id eq id }.toUserHashable()

    override fun getUserHashableByEmail(email: String): User? =
        UsersTable.select { UsersTable.email eq email }.toUserHashable()

    override fun getUsers(): List<User> =
        UsersTable.selectAll().toUsers()

    override fun insertUser(user: InsertNewUser): User? =
        UsersTable.insert {
            val time = LocalDateTime.now().toDatabaseString()

            it[firstName] = user.firstName
            it[lastName] = user.lastName
            it[email] = user.email
            it[password] = user.password
            it[createdAt] = time
            it[updatedAt] = time
        }.resultedValues?.toUsers()?.singleOrNull()

    override fun updateUser(id: Int, user: UpdateUser): User? {
        if (user.hasData()) {
            UsersTable.update({ UsersTable.id eq id }) {
                user.firstName?.let { first -> it[firstName] = first }
                user.lastName?.let { last -> it[lastName] = last }
                user.email?.let { mail -> it[email] = mail }

                it[updatedAt] = LocalDateTime.now().toDatabaseString()
            }
        }

        return getUser(id)
    }

    override fun deleteUser(id: Int): Boolean =
        UsersTable.deleteWhere { UsersTable.id eq id } > 0

    override fun userUnique(email: String): Boolean =
        UsersTable.select { UsersTable.email eq email }.empty()

    override fun isUserRoleAdmin(userId: Int): Boolean =
        UsersTable.select { UsersTable.id eq userId }.firstOrNull()?.get(UsersTable.role) == UserRoles.Admin

    override fun updateUserPassword(userId: Int, updatePassword: String): User? {
        UsersTable.update({ UsersTable.id eq userId }) {
            it[password] = updatePassword
            it[updatedAt] = LocalDateTime.now().toDatabaseString()
        }

        return getUser(userId)
    }
}