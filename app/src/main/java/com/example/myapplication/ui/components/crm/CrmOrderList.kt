package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderListScreen(
  onBack: () -> Unit,
  navigateCrmOrderDetails: (Int) -> Unit,
  customerId: Int
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(true) }
  var orders by remember { mutableStateOf<List<WholesaleOrderDto>>(emptyList()) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LaunchedEffect(customerId) {
    isLoading = true
    errorMessage = null
    try {
      val orderService = NetworkModule.getWholesaleOrderService(context)
      orders = orderService.getAllOrdersByCustomer(customerId)
    } catch (e: Exception) {
      errorMessage = "Не вдалося завантажити замовлення. Спробуйте пізніше."
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Список замовлень") },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        }
      )
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
        Text(errorMessage!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
      } else if (orders.isEmpty()) {
        Text("Немає доступних замовлень.", style = MaterialTheme.typography.bodyLarge)
      } else {
        Text(
          text = "Ваші замовлення:",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        orders.forEach { order ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(bottom = 8.dp)
              .clickable { navigateCrmOrderDetails(order.id) }
              .background(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.medium
              )
              .padding(16.dp)
          ) {
            Column {
              Text(
                text = "Замовлення #${order.id}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              Text(
                text = "Сума замовлення: ${order.totalPrice} грн",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              Text(
                text = "Статус: ${order.status}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              Text(
                text = "Дата створення: ${order.createdAt}",
                style = MaterialTheme.typography.bodySmall
              )
            }
          }
        }
      }
    }
  }
}
