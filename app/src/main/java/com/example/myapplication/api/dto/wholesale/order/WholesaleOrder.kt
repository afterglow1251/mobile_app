package com.example.myapplication.api.dto.wholesale.order

import com.example.myapplication.api.dto.wholesale.customer.WholesaleCustomerDto

data class WholesaleOrderDto(
  val id: Int,
  val totalPrice: Double,
  val status: String,
  val createdAt: String,
  val customer: WholesaleCustomerDto,
  val orderItems: List<WholesaleOrderItemDto>
)