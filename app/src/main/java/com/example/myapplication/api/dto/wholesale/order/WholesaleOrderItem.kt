package com.example.myapplication.api.dto.wholesale.order

data class WholesaleOrderItemDto(
  val productId: Int,
  val quantity: Int,
  val price: Double
)