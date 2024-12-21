package com.example.myapplication.ui.components.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.utils.LocalStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(userId: Int, onBack: () -> Unit) {
  val context = LocalContext.current
  var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

  LaunchedEffect(userId) {
    cartItems = LocalStorage.getCart(context).filter { it.userId == userId }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Кошик") },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
          }
        }
      )
    }
  ) { innerPadding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Text("Кошик порожній", fontSize = 20.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        items(cartItems) { cartItem ->
          CartItemRow(cartItem = cartItem, onRemove = {
            LocalStorage.removeFromCart(context, cartItem.productId)
            cartItems = cartItems.filter { it.productId != cartItem.productId }
          })
        }
      }
    }
  }
}

@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(text = cartItem.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
      Text(text = "Ціна: ${cartItem.price} грн", fontSize = 16.sp)
      Text(text = "Кількість: ${cartItem.quantity}", fontSize = 16.sp)
    }
    Button(
      onClick = onRemove,
      modifier = Modifier.padding(start = 8.dp)
    ) {
      Text("Видалити")
    }
  }
}
