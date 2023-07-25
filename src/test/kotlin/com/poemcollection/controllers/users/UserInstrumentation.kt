package com.poemcollection.controllers.users

import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdatePassword
import com.poemcollection.data.dto.requests.user.UpdateUser
import com.poemcollection.domain.models.user.User

object UserInstrumentation {

    fun givenAnInvalidInsertUser() = InsertNewUser("", "", "John.Doe@example.be", "Test1234", "Test1234")

    fun givenAnAlreadyKnownInsertUser() = InsertNewUser("John", "Doe", "John.Doe@example.be", "Test1234", "Test1234")

    fun givenAnInvalidInsertUserWherePasswordsDontMatch() = InsertNewUser("John", "Doe", "John.Doe@example.be", "Test1234", "Test12345")

    fun givenAnInvalidInsertUserWherePasswordIsNotStrong() = InsertNewUser("John", "Doe", "John.Doe@example.be", "test", "test")

    fun givenAValidInsertUser() = InsertNewUser("John", "Doe", "John.Doe@example.be", "Test1234", "Test1234")

    fun givenAValidUser() = User(1, "John", "Doe", "john.doe@example.be", "", "")

    fun givenAValidUpdateUser() = UpdateUser("John", "Doe", "john.doe@example.be")

    fun givenOldPasswordIsSameAsNewPassword() = UpdatePassword("Test1234", "Test1234", "Test1234")

    fun givenPasswordsDontMatch() = UpdatePassword("Test1", "Test1234", "1234Test")

    fun givenPasswordNotStrong() = UpdatePassword("Test1", "test", "test")

    fun givenValidUpdatePassword() = UpdatePassword("Test1", "Test1234", "Test1234")
}