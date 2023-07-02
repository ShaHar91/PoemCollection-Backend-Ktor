package com.poemcollection.data.dto.requests.user

interface PasswordAble {

    val password: String
    val repeatPassword: String?

    val isPasswordSame get() = password == repeatPassword

    // Password should at least be 8 characters long AND should contain at least 1 capital letter
    val isPasswordStrong get() = password.length >= 8 && password.contains(Regex("[A-Z]"))
}

interface NameAble {

    val firstName: String?
    val lastName: String?
}