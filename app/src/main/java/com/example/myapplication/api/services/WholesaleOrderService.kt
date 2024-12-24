package com.example.myapplication.api.services

import com.example.myapplication.api.dto.wholesale.order.CreateWholesaleOrderDto
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import retrofit2.http.*

interface WholesaleOrderService {

  @GET("wholesale-orders")
  suspend fun getAllOrders(): List<WholesaleOrderDto>

  @GET("wholesale-orders/{id}")
  suspend fun getOrderById(@Path("id") id: Int): WholesaleOrderDto

  @POST("wholesale-orders/create")
  suspend fun createOrder(
    @Body createDto: CreateWholesaleOrderDto
  ): WholesaleOrderDto

  @GET("wholesale-orders/customer/{customerId}/latest")
  suspend fun getLatestOrdersByCustomer(
    @Path("customerId") customerId: Int
  ): List<WholesaleOrderDto>
}
