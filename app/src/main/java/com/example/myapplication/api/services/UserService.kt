package com.example.myapplication.api.services

import com.example.myapplication.api.dto.user.LoginDto
import com.example.myapplication.api.dto.user.LoginResponseDto
import com.example.myapplication.api.dto.user.RegisterDto
import com.example.myapplication.api.dto.user.UserDto
import retrofit2.http.*

interface UserService {

    @GET("users")
    suspend fun getUsers(@Header("Authorization") authHeader: String): List<UserDto>

    @GET("users/profile")
    suspend fun getUserProfile(@Header("Authorization") authHeader: String): UserDto

    @POST("users/register")
    suspend fun registerUser(@Body registerDto: RegisterDto): UserDto

    @POST("users/login")
    suspend fun loginUser(@Body loginDto: LoginDto): LoginResponseDto
}