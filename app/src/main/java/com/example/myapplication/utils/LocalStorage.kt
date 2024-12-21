package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.api.dto.user.UserDto
import com.google.gson.Gson

object LocalStorage {

    private const val PREF_NAME = "user_prefs"
    private const val TOKEN_KEY = "auth_token"
    private const val USER_KEY = "user_data"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(TOKEN_KEY, null)
    }

    fun removeToken(context: Context) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.remove(TOKEN_KEY)
        editor.apply()
    }

    fun saveUser(context: Context, user: UserDto) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        val userJson = Gson().toJson(user)
        editor.putString(USER_KEY, userJson)
        editor.apply()
    }

    fun getUser(context: Context): UserDto? {
        val prefs = getPreferences(context)
        val userJson = prefs.getString(USER_KEY, null)
        return if (userJson != null) {
            Gson().fromJson(userJson, UserDto::class.java)
        } else {
            null
        }
    }

    fun removeUser(context: Context) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        editor.remove(USER_KEY)
        editor.apply()
    }
}
