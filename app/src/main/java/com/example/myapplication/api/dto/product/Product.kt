package com.example.myapplication.api.dto.product

data class ProductDto(
  val id: Int,
  val name: String,
  val description: String,
  val price: Double,
  val quantity: Int,
  val categoryId: Int,
  val images: List<ProductImageDto>
)