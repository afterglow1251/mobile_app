package com.example.myapplication.api.dto.wholesale.order

import com.example.myapplication.api.dto.product.ProductDto

data class WholesaleOrderItemDto(
  val id: Int,
  val quantity: Int,
  val price: Double,
  val product: ProductDto,
)