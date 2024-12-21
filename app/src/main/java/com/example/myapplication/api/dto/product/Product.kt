package com.example.myapplication.api.dto.product

data class ProductDto(
  val id: Int,
  val name: String,
  val description: String,
  val price: Double,
  val quantity: Int,
  val category: CategoryDto,
  val images: List<ProductImageDto>,
)