package com.example.myapplication.ui.components.crm

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@SuppressLint("NewApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderDetailsScreen(orderId: Int, onBack: () -> Unit) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(true) }
  var orderDetails by remember { mutableStateOf<WholesaleOrderDto?>(null) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

  LaunchedEffect(orderId) {
    coroutineScope.launch {
      try {
        val orderService = NetworkModule.getWholesaleOrderService(context)
        orderDetails = orderService.getOrderById(orderId)
      } catch (e: Exception) {
        errorMessage = "Помилка завантаження замовлення: ${e.localizedMessage}"
      } finally {
        isLoading = false
      }
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
    bottomBar = {
      Button(
        onClick = { showDeleteConfirmationDialog = true },
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp),
      ) {
        Text("Видалити замовлення", style = MaterialTheme.typography.titleMedium)
      }
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      if (isLoading) {
        Box(
          modifier = Modifier
            .fillMaxSize(),
          contentAlignment = Alignment.Center
        ) {
          CircularProgressIndicator()
        }
      } else if (errorMessage != null) {
        Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
      } else if (orderDetails != null) {
        val order = orderDetails!!
        val formattedDate = ZonedDateTime.parse(order.createdAt)
          .withZoneSameInstant(ZoneId.of("Europe/Kiev"))
          .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))

        Text(
          text = "Замовлення #${order.id}",
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
          text = "Дата створення: $formattedDate",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
          text = "Загальна сума: ${order.totalPrice} грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
          text = "Список товарів:",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Column {
          order.orderItems.forEachIndexed { index, item ->
            Text(
              text = "- ${item.product.name}: x${item.quantity}, ${item.price * item.quantity} грн",
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }
        }
      } else {
        Text("Замовлення не знайдено.")
      }
    }

    if (showDeleteConfirmationDialog) {
      AlertDialog(
        onDismissRequest = { showDeleteConfirmationDialog = false },
        text = {
          Text("Ви впевнені, що хочете видалити це замовлення?")
        },
        confirmButton = {
          TextButton(onClick = {
            coroutineScope.launch {
              try {
                val orderService = NetworkModule.getWholesaleOrderService(context)
                orderService.deleteOrder(orderId)
                onBack()
              } catch (e: Exception) {
                errorMessage = "Помилка видалення замовлення: ${e.localizedMessage}"
              } finally {
                showDeleteConfirmationDialog = false
              }
            }
          }) {
            Text("Підтвердити")
          }
        },
        dismissButton = {
          TextButton(onClick = { showDeleteConfirmationDialog = false }) {
            Text("Скасувати")
          }
        },
        modifier = Modifier.padding(16.dp)
      )
    }
  }
}
