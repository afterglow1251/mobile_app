package com.example.myapplication.validators

fun isValidPhoneNumber(phone: String): Boolean {
  val phoneRegex = "^\\+?\\d{10,15}\$".toRegex()
  return phone.matches(phoneRegex)
}
