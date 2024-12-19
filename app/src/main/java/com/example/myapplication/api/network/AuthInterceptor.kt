package com.example.myapplication.api.network

import com.example.myapplication.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = TokenManager.getToken(context)

        val newRequest = if (!token.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
