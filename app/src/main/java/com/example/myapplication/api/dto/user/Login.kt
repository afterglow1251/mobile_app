package com.example.myapplication.api.dto.user

data class LoginDto(
    val email: String,
    val password: String
)

data class LoginResponseDto(
    val token: String
)