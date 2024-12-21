package com.example.myapplication.api.dto.product

data class CartItem(
  val userId: Int,
  val productId: Int,
  val name: String,
  val description: String,
  val price: Double,
  val category: CategoryDto, // Category of the product
  val imageUrl: String,     // URL of the product image
  var quantity: Int         // Quantity of the product in the cart
)