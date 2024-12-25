package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import com.example.myapplication.api.network.NetworkModule
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmArchiveMonthScreen(
  onBack: () -> Unit,
) {
  val context = LocalContext.current
  val customerService = NetworkModule.getWholesaleCustomerService(context)
  val coroutineScope = rememberCoroutineScope()
  val monthsInUkrainian = listOf(
    "Січень", "Лютий", "Березень", "Квітень", "Травень",
    "Червень", "Липень", "Серпень", "Вересень", "Жовтень",
    "Листопад", "Грудень"
  )
  var ordersByMonth by remember { mutableStateOf<Map<String, List<WholesaleOrderDto>>>(emptyMap()) }
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    isLoading = true
    try {
      val allOrders = customerService.getAllCustomers().flatMap { it.orders }
      ordersByMonth = allOrders.groupBy {
        val orderDate = parseDate(it.createdAt)
        if (orderDate != null) {
          val calendar = Calendar.getInstance().apply { time = orderDate }
          val year = calendar.get(Calendar.YEAR)
          val month = calendar.get(Calendar.MONTH)
          "$year-${monthsInUkrainian[month]}"
        } else "Unknown"
      }.toSortedMap(compareByDescending {
        val (year, month) = it.split("-")
        val monthIndex = monthsInUkrainian.indexOf(month)
        year.toInt() * 12 + monthIndex
      })
    } catch (e: Exception) {
      // Handle exceptions
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Статистика за місяці") },
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
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      if (isLoading) {
        Text("Завантаження...")
      } else {
        ordersByMonth.forEach { (month, orders) ->
          Text(
            text = month,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
          )
          SalesChartMonthlyAny(orders = orders)
          val totalSales = orders.sumOf { it.totalPrice.toInt() }
          Spacer(modifier = Modifier.height(24.dp))
          Text(
            text = "Обсяг продажів за місяць: ${totalSales} грн",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
          )
        }
      }
    }
  }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmArchiveYearScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val customerService = NetworkModule.getWholesaleCustomerService(context)
  val coroutineScope = rememberCoroutineScope()
  var ordersByYear by remember { mutableStateOf<Map<String, List<WholesaleOrderDto>>>(emptyMap()) }
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    isLoading = true
    try {
      val allOrders = customerService.getAllCustomers().flatMap { it.orders }
      ordersByYear = allOrders.groupBy {
        val orderDate = parseDate(it.createdAt)
        if (orderDate != null) {
          val calendar = Calendar.getInstance().apply { time = orderDate }
          calendar.get(Calendar.YEAR).toString()
        } else "Unknown"
      }.toSortedMap(compareByDescending { it.toIntOrNull() ?: 0 })
    } catch (e: Exception) {
      // Handle exceptions
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Статистика за роки") },
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
        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      if (isLoading) {
        Text("Завантаження...")
      } else {
        ordersByYear.forEach { (year, orders) ->
          Text(
            text = year,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
          )
          SalesChartYearlyAny(orders = orders)
          val totalSales = orders.sumOf { it.totalPrice.toInt() }
          Spacer(modifier = Modifier.height(24.dp))
          Text(
            text = "Обсяг продажів за рік: ${totalSales} грн",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
          )
        }
      }
    }
  }
}
