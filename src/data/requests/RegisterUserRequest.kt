package com.example.data.requests

data class RegisterUserRequest(
    val email: String,
    val password: String,
    var token: String = ""
)
