package com.example.myapplication.api.dto.wholesale.order

data class CreateWholesaleOrderItemDto(
  val productId: Int,
  val quantity: Int
)