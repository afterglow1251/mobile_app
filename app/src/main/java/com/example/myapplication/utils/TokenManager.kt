package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {

    private const val PREF_NAME = "user_prefs"
    private const val TOKEN_KEY = "auth_token"

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
}
