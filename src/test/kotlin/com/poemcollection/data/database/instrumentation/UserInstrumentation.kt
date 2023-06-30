package com.poemcollection.data.database.instrumentation

import com.poemcollection.domain.models.SaltedHash
import com.poemcollection.domain.models.user.InsertNewUser
import com.poemcollection.domain.models.user.UpdatePassword
import com.poemcollection.domain.models.user.UpdateUser

object UserInstrumentation {

    fun givenAValidInsertUserBody() = InsertNewUser("christiano", "bolla", "christiano@example", SaltedHash("hash", "salt"))

    fun givenASecondValidInsertUserBody() = InsertNewUser("Jane", "Ode", "jane@example", SaltedHash("hash", "salt"))

    fun givenAValidUpdateUserBody() = UpdateUser("John", "Doe", "john.dao@example.be")

    fun givenAnEmptyUpdateUserBody() = UpdateUser()

    fun givenAValidUpdateUserPasswordBody() = UpdatePassword(SaltedHash("new-hash", "new-salt"))
}