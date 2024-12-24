package com.example.myapplication.api.network

import com.example.myapplication.api.services.UserService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import com.example.myapplication.api.services.ProductService
import com.example.myapplication.api.services.WholesaleCustomerService
import com.example.myapplication.api.services.WholesaleOrderService

object NetworkModule {

    private const val BASE_URL = "http://10.0.2.2:3000/"

    private fun createOkHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }

    private fun createRetrofit(context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient(context))
            .build()
    }

    fun getUserService(context: Context): UserService {
        return createRetrofit(context).create(UserService::class.java)
    }

    fun getProductService(context: Context): ProductService {
        return createRetrofit(context).create(ProductService::class.java)
    }

    fun getWholesaleCustomerService(context: Context): WholesaleCustomerService {
        return createRetrofit(context).create(WholesaleCustomerService::class.java)
    }

    fun getWholesaleOrderService(context: Context): WholesaleOrderService {
        return createRetrofit(context).create(WholesaleOrderService::class.java)
    }
}
