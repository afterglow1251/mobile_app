package com.example.myapplication.api.services

import com.example.myapplication.api.dto.product.ProductDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {

  @GET("products")
  suspend fun getAllProducts(): List<ProductDto>

  @GET("products/{id}")
  suspend fun getProductById(@Path("id") id: Int): ProductDto

  @GET("products/category/{categoryName}")
  suspend fun getProductsByCategory(@Path("categoryName") categoryName: String): List<ProductDto>
}
