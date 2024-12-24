package com.example.myapplication.ui.components.crm

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.example.myapplication.api.dto.wholesale.customer.WholesaleCustomerDto
import androidx.compose.foundation.text.BasicText
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import com.example.myapplication.api.network.NetworkModule
import java.text.SimpleDateFormat
import java.util.*

fun parseDate(dateString: String): Date? {
  return try {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    formatter.parse(dateString)
  } catch (e: Exception) {
    null
  }
}

fun isDateInCurrentWeek(date: Date): Boolean {
  val calendar = Calendar.getInstance()
  val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)
  val currentYear = calendar.get(Calendar.YEAR)

  calendar.time = date
  val dateWeek = calendar.get(Calendar.WEEK_OF_YEAR)
  val dateYear = calendar.get(Calendar.YEAR)

  return dateWeek == currentWeek && dateYear == currentYear
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmMainScreen(
  onBack: () -> Unit,
  navigateToCrmClientList: () -> Unit,
  navigateToCrmStats: () -> Unit
) {
  val context = LocalContext.current
  var orders by remember { mutableStateOf<List<WholesaleOrderDto>>(emptyList()) }
  var isLoading by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var customers by remember { mutableStateOf<List<WholesaleCustomerDto>>(emptyList()) }

  val totalSales = "%.2f".format(orders.sumOf { it.totalPrice })
  val totalCustomers = customers.size

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

  LaunchedEffect(Unit) {
    try {
      val service = NetworkModule.getWholesaleOrderService(context)
      orders = service.getAllOrders().filter { order ->
        val orderDate = parseDate(order.createdAt)
        orderDate != null && isDateInCurrentWeek(orderDate)
      }
    } catch (e: Exception) {
      errorMessage = "Не вдалося завантажити замовлення: ${e.localizedMessage}"
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("CRM Система") },
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
        .padding(top = 0.dp, start = 16.dp, end = 16.dp)
    ) {

      Text(
        text = "Активні клієнти: $totalCustomers",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
        text = "Обсяг продажів: $totalSales грн",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      WeeklySalesChart(orders)

      Spacer(modifier = Modifier.height(32.dp))

      Button(
        onClick = navigateToCrmClientList,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Перейти до клієнтів")
      }

      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = navigateToCrmStats,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Перейти до статистики")
      }
    }
  }
}

@Composable
fun WeeklySalesChart(orders: List<WholesaleOrderDto>) {
  val calendar = Calendar.getInstance()
  calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
  val startOfWeek = calendar.time
  val today = Calendar.getInstance()
  val currentDayIndex = today.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY
  val salesData = (0..6).map { day ->
    calendar.time = startOfWeek
    calendar.add(Calendar.DAY_OF_YEAR, day)
    val currentDate = calendar.time
    if (day > currentDayIndex) 0.0 else orders.filter {
      val orderDate = parseDate(it.createdAt)
      orderDate != null && SimpleDateFormat("yyyy-MM-dd").format(orderDate) == SimpleDateFormat("yyyy-MM-dd").format(currentDate)
    }.sumOf { it.totalPrice.toDouble() }
  }
  val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")

  val buttonColor = MaterialTheme.colorScheme.primary

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = "Графік продажів за тиждень",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(bottom = 16.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))

    Canvas(modifier = Modifier.fillMaxSize()) {
      val maxSales = salesData.maxOrNull() ?: 1.0
      val horizontalSpacing = (size.width - 48.dp.toPx()) / 6
      val offsetStart = 32.dp.toPx()
      val verticalStep = size.height / 5

      // Extend the gray vertical line slightly above the top of the chart
      drawLine(
        color = Color.Gray,
        start = Offset(16.dp.toPx(), -16.dp.toPx()),
        end = Offset(16.dp.toPx(), size.height),
        strokeWidth = 1.dp.toPx()
      )

      for (i in 0..5) {
        val y = size.height - i * verticalStep
        drawLine(
          color = Color.LightGray,
          start = Offset(16.dp.toPx(), y),
          end = Offset(size.width, y),
          strokeWidth = 1.dp.toPx()
        )
        drawContext.canvas.nativeCanvas.drawText(
          "${"%.2f".format(maxSales * i / 5)}",
          12.dp.toPx(),
          y + 6.dp.toPx(),
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.RIGHT
            textSize = 32f
          }
        )
      }

      for (i in 0..currentDayIndex) {
        val startX = offsetStart + i * horizontalSpacing
        val startY = size.height - (salesData[i] / maxSales * size.height).toFloat()
        if (i < currentDayIndex) {
          val endX = offsetStart + (i + 1) * horizontalSpacing
          val endY = size.height - (salesData[i + 1] / maxSales * size.height).toFloat()

          drawLine(
            color = buttonColor,
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 4.dp.toPx()
          )
        }

        drawCircle(
          color = buttonColor,
          center = Offset(startX, startY),
          radius = 6.dp.toPx()
        )
      }

      days.forEachIndexed { index, day ->
        val x = offsetStart + index * horizontalSpacing
        val y = size.height + 20.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
          day,
          x,
          y,
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 32f
          }
        )
      }
    }
  }
}
