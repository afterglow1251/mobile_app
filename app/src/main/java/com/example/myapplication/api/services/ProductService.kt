package com.example.myapplication.api.services

import com.example.myapplication.api.dto.order.GetOrdersResponse
import com.example.myapplication.api.dto.order.Order
import com.example.myapplication.api.dto.order.OrderResponse
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.dto.user.UpdateDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {

  @GET("products")
  suspend fun getAllProducts(
    @Query("name") name: String? = null,
    @Query("minPrice") minPrice: Double? = null,
    @Query("maxPrice") maxPrice: Double? = null,
    @Query("unitSize") unitSize: String? = null,
    @Query("beerType") beerType: String? = null,
    @Query("manufacturerName") manufacturerName: String? = null,
    @Query("manufacturerCountry") manufacturerCountry: String? = null,
    @Query("category") category: String? = null,
  ): List<ProductDto>

  @GET("products/{id}")
  suspend fun getProductById(@Path("id") id: Int): ProductDto

  @GET("products/category/{categoryName}")
  suspend fun getProductsByCategory(@Path("categoryName") categoryName: String): List<ProductDto>

  @POST("orders/create")
  suspend fun createOrder(
    //@Header("Authorization") authHeader: String,
    @Body order: Order
  ): OrderResponse

  @GET("orders")
  suspend fun getOrders(
    //@Header("Authorization") authHeader: String,
  ): List<GetOrdersResponse>

}
