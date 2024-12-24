package com.example.myapplication.api.services

import com.example.myapplication.api.dto.wholesale.customer.CreateWholesaleCustomerDto
import com.example.myapplication.api.dto.wholesale.customer.UpdateWholesaleCustomerDto
import com.example.myapplication.api.dto.wholesale.customer.WholesaleCustomerDto
import retrofit2.http.*

interface WholesaleCustomerService {

  @GET("wholesale-customers")
  suspend fun getAllCustomers(): List<WholesaleCustomerDto>

  @GET("wholesale-customers/{id}")
  suspend fun getCustomerById(@Path("id") id: Int): WholesaleCustomerDto

  @POST("wholesale-customers")
  suspend fun createCustomer(
    @Body createDto: CreateWholesaleCustomerDto
  ): WholesaleCustomerDto

  @PUT("wholesale-customers/{id}")
  suspend fun updateCustomer(
    @Path("id") id: Int,
    @Body updateDto: UpdateWholesaleCustomerDto
  ): WholesaleCustomerDto

  @DELETE("wholesale-customers/{id}")
  suspend fun deleteCustomer(@Path("id") id: Int)
}