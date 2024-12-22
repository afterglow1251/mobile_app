package com.example.myapplication.ui.components.cart

import android.util.Log
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
import com.example.myapplication.api.dto.order.Order
import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.api.dto.user.UpdateDto
import com.example.myapplication.api.dto.user.UserDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.utils.LocalStorage
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(userId: Int, onBack: () -> Unit) {
  val context = LocalContext.current
  var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
  var totalPrice by remember { mutableStateOf(0.0) }
  var showOrderDialog by remember { mutableStateOf(false) }
  val userDto = LocalStorage.getUser(context)
  val productService = NetworkModule.getProductService(context)

  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(userId) {
    cartItems = LocalStorage.getCart(context).filter { it.userId == userId }
    totalPrice = cartItems.sumOf { it.price * it.quantity }
  }

  if (showOrderDialog) {
    OrderDialog(
      userDto = userDto,
      onDismiss = { showOrderDialog = false },
      onConfirm = { name, phone, address ->

        var isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
          val requestBody = Order(
            shippingAddress = address,
            username = name,
            phoneNumber = phone,
            items = LocalStorage.getCartForBackend(context) // Використання функції getCartForBackend
          )

          try {

            val createOrder = productService.createOrder(
              // authHeader = "Bearer ${LocalStorage.getToken(context)}",
              order = requestBody
            )

            withContext(Dispatchers.Main) {
              LocalStorage.clearCartForUser(context)
              isLoading = false
              successMessage = "Замовлення створено"
            }
          } catch (e: Exception) {
            withContext(Dispatchers.Main) {
              isLoading = false
              errorMessage = "Помилка оновлення: ${e.localizedMessage}"
            }
          }
        }

        showOrderDialog = false
        onBack()
      }
    )
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
    },
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Text(
            text = "Загальна вартість: ${"%.2f".format(totalPrice)} грн",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
          )
          Button(
            onClick = { showOrderDialog = true },
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Оформити замовлення", fontSize = 18.sp)
          }
        }
      }
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
            totalPrice = cartItems.sumOf { it.price * it.quantity }
          })
        }
      }
    }
  }
}

@Composable
fun CartItemRow(cartItem: CartItem, onRemove: () -> Unit) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(text = cartItem.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Ціна: ${cartItem.price} грн", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
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
}

@Composable
fun OrderDialog(userDto: UserDto?, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
  var name by remember { mutableStateOf(userDto?.username ?: "") }
  var phone by remember { mutableStateOf(userDto?.phoneNumber ?: "") }
  var address by remember { mutableStateOf(userDto?.address ?: "") }

  AlertDialog(
    onDismissRequest = { onDismiss() },
    title = { Text("Підтвердження замовлення") },
    text = {
      Column {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Ім'я") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Номер телефону") },
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Адреса") },
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(onClick = { onConfirm(name, phone, address) }) {
        Text("Підтвердити")
      }
    },
    dismissButton = {
      TextButton(onClick = { onDismiss() }) {
        Text("Скасувати")
      }
    }
  )
}
