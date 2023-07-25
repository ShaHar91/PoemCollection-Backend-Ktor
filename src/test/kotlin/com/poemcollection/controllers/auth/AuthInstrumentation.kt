package com.poemcollection.controllers.auth

import com.poemcollection.data.dto.requests.CreateTokenDto

object AuthInstrumentation {
    fun givenAValidCreateToken() = CreateTokenDto("john@example.be", "Test1234")
    fun givenAnInvalidCreateToken() = CreateTokenDto("john", "Test1234")
}