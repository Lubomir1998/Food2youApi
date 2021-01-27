package com.example.routes

data class RegisterUserRequest(
    val email: String,
    val password: String,
    var token: String = ""
)
