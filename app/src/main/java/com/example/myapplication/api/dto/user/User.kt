package com.example.myapplication.api.dto.user

data class UserDto(
  val id: Int,
  val email: String,
  val username: String,
  val phoneNumber: String?,
  val address: String?,
)
