package com.example.myapplication.api.dto.wholesale.customer

import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto

data class WholesaleCustomerDto(
  val id: Int,
  val name: String,
  val address: String,
  val phoneNumber: String,
  val createdAt: String,
  val orders: List<WholesaleOrderDto>,
)

