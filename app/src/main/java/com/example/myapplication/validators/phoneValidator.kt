package com.example.myapplication.validators

fun isValidPhoneNumber(phone: String): Boolean {
  val phoneRegex = "^\\+38\\d{10}\$".toRegex()
  return phone.matches(phoneRegex)
}
