package com.example.myapplication.api.dto.wholesale.order

data class CreateWholesaleOrderDto(
  val customerId: Int,
  val items: List<CreateWholesaleOrderItemDto>
)