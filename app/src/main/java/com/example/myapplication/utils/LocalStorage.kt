package com.example.myapplication.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.myapplication.api.dto.order.CartItemBackend
import com.example.myapplication.api.dto.user.UserDto
import com.example.myapplication.api.dto.product.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object LocalStorage {

    private const val PREF_NAME = "user_prefs"
    private const val TOKEN_KEY = "auth_token"
    private const val USER_KEY = "user_data"
    private const val CART_KEY = "cart_items"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // --- Токен ---
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

    // --- Користувач ---
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

    // --- Кошик ---
    fun addToCart(context: Context, cartItem: CartItem) {
        val user = getUser(context)
        if (user != null) {
            val cartItems = getCart(context).toMutableList()

            val existingItem = cartItems.find { it.productId == cartItem.productId && it.userId == user.id }
            if (existingItem != null) {
                existingItem.quantity += cartItem.quantity
            } else {
                cartItems.add(cartItem)
            }

            saveCart(context, cartItems)
        }
    }

    fun removeFromCart(context: Context, productId: Int) {
        val cartItems = getCart(context).toMutableList()

        val updatedCartItems = cartItems.filter { it.productId != productId }
        saveCart(context, updatedCartItems)
    }

    fun updateCartItemQuantity(context: Context, productId: Int, newQuantity: Int) {
        val cartItems = getCart(context).toMutableList()

        val cartItem = cartItems.find { it.productId == productId }
        if (cartItem != null) {
            cartItem.quantity = newQuantity
            saveCart(context, cartItems)
        }
    }

    fun getCart(context: Context): List<CartItem> {
        val prefs = getPreferences(context)
        val user = getUser(context)
        val cartJson = prefs.getString(CART_KEY, null)
        return if (user != null && cartJson != null) {
            val type = object : TypeToken<List<CartItem>>() {}.type
            Gson().fromJson<List<CartItem>>(cartJson, type).filter { it.userId == user.id }
        } else {
            emptyList()
        }
    }

    private fun saveCart(context: Context, cartItems: List<CartItem>) {
        val prefs = getPreferences(context)
        val editor = prefs.edit()
        val cartJson = Gson().toJson(cartItems)
        editor.putString(CART_KEY, cartJson)
        editor.apply()
    }

    fun clearCartForUser(context: Context) {
        val user = getUser(context)
        if (user != null) {
            val cartItems = getCart(context).toMutableList()

            val updatedCartItems = cartItems.filter { it.userId != user.id }
            saveCart(context, updatedCartItems)
        }
    }

    fun getCartForBackend(context: Context): List<CartItemBackend> {
        val user = getUser(context)
        return if (user != null) {
            getCart(context).filter { it.userId == user.id }.map { cartItem ->
                CartItemBackend(
                    quantity = cartItem.quantity,
                    productId = cartItem.productId
                )
            }
        } else {
            emptyList()
        }
    }

    fun logoutUser(context: Context) {
        clearCartForUser(context)
        removeToken(context)
        removeUser(context)
    }
}
