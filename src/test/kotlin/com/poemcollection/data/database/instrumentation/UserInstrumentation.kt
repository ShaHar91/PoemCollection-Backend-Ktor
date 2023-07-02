package com.poemcollection.data.database.instrumentation

import com.poemcollection.data.dto.requests.user.InsertNewUser
import com.poemcollection.data.dto.requests.user.UpdateUser

object UserInstrumentation {

    fun givenAValidInsertUserBody() = InsertNewUser("christiano", "bolla", "christiano@example", "hash", null)

    fun givenAValidUpdateUserBody() = UpdateUser("John", "Doe", "john.dao@example.be")

    fun givenAnEmptyUpdateUserBody() = UpdateUser()
}