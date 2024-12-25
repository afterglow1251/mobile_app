package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.customer.WholesaleCustomerDto
import com.example.myapplication.api.network.NetworkModule
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
  onBack: () -> Unit,
  navigateToCrmClientDetails: (Int) -> Unit,
  navigateCrmClientAdd: () -> Unit
) {
  val context = LocalContext.current
  var customers by remember { mutableStateOf<List<WholesaleCustomerDto>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Форматери для дат
  val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
  val utcDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
    timeZone = TimeZone.getTimeZone("UTC")
  }

  // Завантаження даних
  LaunchedEffect(Unit) {
    try {
      val service = NetworkModule.getWholesaleCustomerService(context)
      customers = service.getAllCustomers()
    } catch (e: Exception) {
      errorMessage = "Failed to load customers: ${e.localizedMessage}"
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Список клієнтів") },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        },
      )
    },
    content = { innerPadding ->
      when {
        isLoading -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            contentAlignment = Alignment.Center
          ) {
            CircularProgressIndicator()
          }
        }
        errorMessage != null -> {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = errorMessage ?: "Unknown error",
              color = MaterialTheme.colorScheme.error,
              style = MaterialTheme.typography.bodyLarge
            )
          }
        }
        else -> {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
              .padding(16.dp)
              .verticalScroll(rememberScrollState())
          ) {
            Button(
              onClick = navigateCrmClientAdd,
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
            ) {
              Text("Додати оптового клієнта")
            }

            Text(
              text = "Ваші клієнти:",
              style = MaterialTheme.typography.titleMedium,
              modifier = Modifier.padding(bottom = 16.dp)
            )

            customers.forEach { customer ->
              val totalOrders = customer.orders.size
              val totalSpent = customer.orders.sumOf { it.totalPrice.toDouble() }
              val lastOrderDate = if (customer.orders.isNotEmpty()) {
                try {
                  val date = utcDateFormatter.parse(
                    customer.orders.maxByOrNull { it.createdAt }?.createdAt ?: ""
                  )
                  dateFormatter.timeZone = TimeZone.getTimeZone("Europe/Kyiv")
                  dateFormatter.format(date!!)
                } catch (e: Exception) {
                  "Некоректний формат дати"
                }
              } else {
                "-"
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 8.dp)
                  .clickable { navigateToCrmClientDetails(customer.id) }
                  .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.medium
                  )
                  .padding(16.dp)
              ) {
                Column {
                  Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                  )
                  Text(
                    text = "Кількість замовлень: $totalOrders",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                  )
                  Text(
                    text = "Сума замовлень: ${"%.2f".format(totalSpent)} грн",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                  )
                  Text(
                    text = "Дата останнього замовлення: $lastOrderDate",
                    style = MaterialTheme.typography.bodySmall
                  )
                }
              }
            }
          }
        }
      }
    }
  )
}