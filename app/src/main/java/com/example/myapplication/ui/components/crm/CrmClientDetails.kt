package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.customer.WholesaleCustomerDto
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmClientDetails(
  clientId: Int,
  onBack: () -> Unit,
  navigateCrmOrderList: (Int) -> Unit,
  navigateCrmOrderDetails: (Int) -> Unit,
  navigateCrmOrderAdd: (Int) -> Unit,
  navigateToCrmClientEdit: (Int) -> Unit
) {
  val context = LocalContext.current
  var isLoading by remember { mutableStateOf(true) }
  var clientData by remember { mutableStateOf<WholesaleCustomerDto?>(null) }
  var latestOrders by remember { mutableStateOf(emptyList<com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto>()) }
  var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
  val coroutineScope = rememberCoroutineScope()

  LaunchedEffect(clientId) {
    isLoading = true
    try {
      val customerService = NetworkModule.getWholesaleCustomerService(context)
      val wholesaleOrderService = NetworkModule.getWholesaleOrderService(context)
      clientData = customerService.getCustomerById(clientId)
      latestOrders = wholesaleOrderService.getLatestOrdersByCustomer(clientId)
    } catch (e: Exception) {
      // Обробка помилки
    } finally {
      isLoading = false
    }
  }

  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text(clientData?.name ?: "Завантаження...", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
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
      } else if (clientData != null) {
        Text(
          text = "Ім'я: ${clientData!!.name}",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(4.dp)
        )

        Text(
          text = "Адреса: ${clientData!!.address}",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(4.dp)
        )

        Text(
          text = "Телефон: ${clientData!!.phoneNumber}",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = { navigateToCrmClientEdit(clientId) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF583E23), // Колір фону кнопки
            contentColor = Color.White         // Колір тексту кнопки
          ),
          shape = RoundedCornerShape(4.dp),
        ) {
          Text("Редагувати інформацію про клієнта")
        }

        Button(
          onClick = { navigateCrmOrderAdd(clientId) },
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF583E23), // Колір фону кнопки
            contentColor = Color.White         // Колір тексту кнопки
          ),
          shape = RoundedCornerShape(4.dp),
        ) {
          Text("Додати замовлення")
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {navigateCrmOrderList(clientId)} )
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
          Text(
            text = "Останні замовлення:",
            style = MaterialTheme.typography.titleLarge,
          )
        }

        if (latestOrders.isNotEmpty()) {
          latestOrders.forEach { order ->
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .clickable { navigateCrmOrderDetails(order.id) }
                .background(
                  color = Color(0xFFFBF1DA),
                  shape = MaterialTheme.shapes.medium
                )
                .padding(16.dp)
            ) {
              Column {
                Text(
                  text = "Номер замовлення: ${order.id}",
                  style = MaterialTheme.typography.titleMedium,
                  modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                  text = "Сума: ${order.totalPrice} грн",
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                  text = "Статус: ${order.status}",
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier.padding(bottom = 4.dp)
                )
              }
            }
          }
        } else {
          Text("Немає доступних замовлень.")
        }

        Button(
          onClick = { showDeleteConfirmationDialog = true },
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF583E23), // Колір фону кнопки
            contentColor = Color.White         // Колір тексту кнопки
          ),
          shape = RoundedCornerShape(4.dp),
        ) {
          Text("Видалити клієнта")
        }

        if (showDeleteConfirmationDialog) {
          AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            text = {
              Text("Ви впевнені, що хочете видалити цього клієнта?", color=Color(0xFF583E23))
            },
            confirmButton = {
              TextButton(onClick = {
                coroutineScope.launch {
                  try {
                    val customerService = NetworkModule.getWholesaleCustomerService(context)
                    customerService.deleteCustomer(clientId)
                    onBack()
                  } catch (e: Exception) {
                    // Обробка помилки
                  }
                }
                showDeleteConfirmationDialog = false
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
              //.background(color = Color(0xFFFBF1DA),
            //shape = RoundedCornerShape(8.dp))
          )
        }
      }
    }
  }
}

