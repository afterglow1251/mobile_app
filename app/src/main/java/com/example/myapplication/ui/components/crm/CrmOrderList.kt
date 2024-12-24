package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderListScreen(
  onBack: () -> Unit,
  navigateCrmOrderDetails: () -> Unit
) {
  val orders = remember {
    List(20) { index ->
      Order(
        "Замовлення #${index + 1}",
        (1000..15000).random(),
        "Місто ${index + 1}, вул. Прикладна, ${index + 1}",
        "+38050${(1000000..9999999).random()}"
      )
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
            .clickable { navigateCrmOrderDetails() }
            .background(
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
        ) {
          Column {
            Text(
              text = order.number,
              style = MaterialTheme.typography.titleSmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Сума замовлення: ${order.totalSum} грн",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Адреса: ${order.address}",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Контактний номер: ${order.contactNumber}",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }
    }
  }
}