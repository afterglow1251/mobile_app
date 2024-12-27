package com.example.myapplication.ui.components.order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.order.GetOrdersResponse
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(orderId: Int, onBack: () -> Unit) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  var orderDetails by remember { mutableStateOf<GetOrdersResponse?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  LaunchedEffect(orderId) {
    isLoading = true
    try {
      val productService = NetworkModule.getProductService(context)
      orderDetails = withContext(Dispatchers.IO) {
        productService.getOrders().find { it.id == orderId }
      }
    } catch (e: Exception) {
      scope.launch { snackbarHostState.showSnackbar("Сталася помилка: ${e.localizedMessage}") }
    } finally {
      isLoading = false
    }
  }

  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Деталі замовлення", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
      } else {
        orderDetails?.let { order ->
          OrderCard(order = order, index = 0)
        }
      }
    }
  }
}

@Composable
fun OrderCard(order: GetOrdersResponse, index: Int) {
  val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
  val formattedDate = try {
    val utcDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }
    val date = utcDateFormatter.parse(order.createdAt)

    dateFormatter.timeZone = TimeZone.getTimeZone("Europe/Kyiv")
    dateFormatter.format(date!!)
  } catch (e: Exception) {
    "Невідома дата"
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(Color(0xFFFDF8ED), shape = MaterialTheme.shapes.medium) // Set background color here
      .padding(16.dp)
  ) {
    Text(
      text = "Замовлення",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
      text = "Дата створення: $formattedDate",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = "Загальна сума: ${order.totalPrice} грн.",
      style = MaterialTheme.typography.bodyLarge,
      color = Color(0xFF583E23),
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
      text = "Адреса доставки: ${order.shippingAddress}",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = "Отримувач: ${order.username}",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = "Номер телефону отримувача: ${order.phoneNumber}",
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.padding(bottom = 4.dp)
    )

    Column(modifier = Modifier.padding(top = 8.dp)) {
      Text(
        text = "Товари:",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
      )
      order.orderItems.forEach { item ->
        Text(
          text = "- ${item.product.name} (x${item.quantity}): ${item.price} грн.",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 4.dp)
        )
      }
    }
  }
}