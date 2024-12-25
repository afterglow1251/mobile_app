package com.example.myapplication.validators

fun isValidPassword(password: String): Boolean {
  return password.length >= 6
}