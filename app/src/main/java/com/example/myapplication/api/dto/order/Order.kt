package com.example.myapplication.api.dto.order

import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.api.dto.product.ProductImageDto

data class Order(
  val shippingAddress: String,
  val username: String,
  val phoneNumber: String,
  val items: List<CartItemBackend>,
)

data class OrderResponse(
  val totalPrice: Double,
  val shippingAddress: String,
  val status: String,
  val username: String,
  val phoneNumber: String,
  val userId: User,
  val id: Int,
  val createdAt: String
)

data class User(
  val id: Int
)

data class CartItemBackend(
  var quantity: Int,
  var productId: Int
)

data class ProductOrderItem(
  var id: Int,
  var name: String,
  var description: String,
  var price: Double,
  var quantity: Int,
  var images: List<ProductImageDto>,
)

data class OrderItem(
  var id: Int,
  var quantity: Int,
  var price: Double,
  var product: ProductOrderItem,
)


data class GetOrdersResponse(
  val id: Int,
  val totalPrice: Double,
  val shippingAddress: String,
  val status: String,
  val createdAt: String,
  val username: String,
  val phoneNumber: String,
  val orderItems: List<OrderItem>
)