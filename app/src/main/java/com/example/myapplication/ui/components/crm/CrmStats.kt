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
import com.example.myapplication.api.dto.wholesale.order.WholesaleOrderDto
import com.example.myapplication.api.network.NetworkModule
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmStatsScreen(
  onBack: () -> Unit,
  navigateToCrmArchive: () -> Unit,
) {
  val context = LocalContext.current
  var orders by remember { mutableStateOf<List<WholesaleOrderDto>>(emptyList()) }
  var customers by remember { mutableStateOf<List<WholesaleCustomerDto>>(emptyList()) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val totalSales = orders.sumOf { it.totalPrice }
  val totalCustomers = customers.size
  val totalOrders = orders.size
  val mostActiveCustomer = customers.maxByOrNull { it.orders.size }
  val mostProfitableCustomer = customers.maxByOrNull { customer ->
    customer.orders.sumOf { it.totalPrice }
  }

  LaunchedEffect(Unit) {
    try {
      val customerService = NetworkModule.getWholesaleCustomerService(context)
      customers = customerService.getAllCustomers()
    } catch (e: Exception) {
      errorMessage = "Не вдалося завантажити клієнтів: ${e.localizedMessage}"
    }
  }

  LaunchedEffect(Unit) {
    try {
      val orderService = NetworkModule.getWholesaleOrderService(context)
      orders = orderService.getAllOrders()
    } catch (e: Exception) {
      errorMessage = "Не вдалося завантажити замовлення: ${e.localizedMessage}"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Ваша статистика") },
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
        .padding(horizontal = 16.dp)
    ) {
      if (errorMessage != null) {
        Text(
          text = errorMessage!!,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(vertical = 8.dp)
        )
      } else {
        Text(
          text = "Всього клієнтів: $totalCustomers",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
          text = "Обсяг продажів: %.2f грн".format(totalSales),
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(vertical = 4.dp)
        )
        Text(
          text = "Всього замовлень: $totalOrders",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(vertical = 4.dp)
        )
        mostActiveCustomer?.let {
          Text(
            text = "Найактивніший клієнт: ${it.name}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
          )
        }
        mostProfitableCustomer?.let {
          Text(
            text = "Найприбутковіший клієнт: ${it.name}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
          text = "Обсяг продажів за останній місяць",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        SalesChartMonthly(orders)

        Spacer(modifier = Modifier.height(32.dp))

        Text(
          text = "Обсяг продажів за цей рік",
          style = MaterialTheme.typography.titleMedium,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        SalesChartYearly(orders)

        Spacer(modifier = Modifier.height(32.dp))
        Button(
          onClick = navigateToCrmArchive,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Перейти до архіву статистики")
        }
      }
    }
  }
}

fun isDateInCurrentMonth(date: Date): Boolean {
  val calendar = Calendar.getInstance()
  val currentMonth = calendar.get(Calendar.MONTH)
  val currentYear = calendar.get(Calendar.YEAR)

  calendar.time = date
  val dateMonth = calendar.get(Calendar.MONTH)
  val dateYear = calendar.get(Calendar.YEAR)

  return dateMonth == currentMonth && dateYear == currentYear
}

fun isDateInCurrentYear(date: Date): Boolean {
  val calendar = Calendar.getInstance()
  val currentYear = calendar.get(Calendar.YEAR)

  calendar.time = date
  val dateYear = calendar.get(Calendar.YEAR)

  return dateYear == currentYear
}

@Composable
fun SalesChartMonthly(orders: List<WholesaleOrderDto>) {
  val salesData = (1..31).map { day ->
    orders.filter { order ->
      val orderDate = parseDate(order.createdAt)
      orderDate != null && isDateInCurrentMonth(orderDate) &&
              Calendar.getInstance().apply { time = orderDate }.get(Calendar.DAY_OF_MONTH) == day
    }.sumOf { it.totalPrice }
  }

  val days = listOf("1", "5", "10", "15", "20", "25", "30")
  val maxSales = salesData.maxOrNull()?.takeIf { it > 0 } ?: 1.0 // Ensure non-zero max value

  val buttonColor = MaterialTheme.colorScheme.primary

  Canvas(modifier = Modifier.size(width = 452.dp, height = 200.dp).padding(top = 20.dp)) {
    val horizontalSpacing = ((size.width - 64.dp.toPx()) / (salesData.size - 1).toFloat()) * 1.037f
    val verticalStep = size.height / 5

    drawLine(
      color = Color.Gray,
      start = Offset(36.dp.toPx(), size.height - size.height - 16),
      end = Offset(36.dp.toPx(), size.height),
      strokeWidth = 1.dp.toPx()
    )

    for (i in 0..5) {
      val y = size.height - i * verticalStep
      drawLine(
        color = Color.LightGray,
        start = Offset(36.dp.toPx(), y),
        end = Offset(size.width, y),
        strokeWidth = 1.dp.toPx()
      )
      drawContext.canvas.nativeCanvas.drawText(
        "${String.format("%d", (maxSales * i / 5).roundToInt())}",
        32.dp.toPx(),
        y + 6.dp.toPx(),
        android.graphics.Paint().apply {
          color = android.graphics.Color.BLACK
          textAlign = android.graphics.Paint.Align.RIGHT
          textSize = 36f
        }
      )
    }

    salesData.forEachIndexed { index, value ->
      val startX = 44.dp.toPx() + index * horizontalSpacing
      val startY = size.height - (value / maxSales).toFloat() * size.height
      val endX = if (index < salesData.size - 1) 24.dp.toPx() + (index + 1) * horizontalSpacing else startX
      val endY = if (index < salesData.size - 1) size.height - (salesData[index + 1] / maxSales).toFloat() * size.height else startY

      drawLine(
        color = buttonColor,
        start = Offset(startX, size.height),
        end = Offset(startX, startY),
        strokeWidth = 6.dp.toPx()
      )
    }

    days.forEachIndexed { index, day ->
      if (day == "5") {
        val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.80f
        val y = size.height + 20.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
          day,
          x,
          y,
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 36f
          })
      } else if (day == "10") {
      val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.905f
      val y = size.height + 20.dp.toPx()
      drawContext.canvas.nativeCanvas.drawText(
        day,
        x,
        y,
        android.graphics.Paint().apply {
          color = android.graphics.Color.BLACK
          textAlign = android.graphics.Paint.Align.CENTER
          textSize = 36f
        })
    } else if (day == "25") {
        val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.965f
        val y = size.height + 20.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
          day,
          x,
          y,
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 36f
          })
    } else if (day == "30") {
        val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.972f
        val y = size.height + 20.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
          day,
          x,
          y,
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 36f
          })
    } else if (day == "20") {
    val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.955f
    val y = size.height + 20.dp.toPx()
    drawContext.canvas.nativeCanvas.drawText(
      day,
      x,
      y,
      android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textAlign = android.graphics.Paint.Align.CENTER
        textSize = 36f
      })
  } else {
        val x = 44.dp.toPx() + index * horizontalSpacing * 5 * 0.93f
        val y = size.height + 20.dp.toPx()
        drawContext.canvas.nativeCanvas.drawText(
          day,
          x,
          y,
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.CENTER
            textSize = 36f
          }
        )
      }
    }
  }
}


@Composable
fun SalesChartYearly(orders: List<WholesaleOrderDto>) {
  val salesData = (1..12).map { month ->
    orders.filter { order ->
      val orderDate = parseDate(order.createdAt)
      orderDate != null && isDateInCurrentYear(orderDate) &&
              Calendar.getInstance().apply { time = orderDate }.get(Calendar.MONTH) + 1 == month
    }.sumOf { it.totalPrice }
  }

  val months = listOf("Січ", "Лют", "Бер", "Кві", "Тра", "Чер", "Лип", "Сер", "Вер", "Жов", "Лис", "Гру")
  val maxSales = salesData.maxOrNull()?.takeIf { it > 0 } ?: 1.0 // Ensure non-zero max value

  val buttonColor = MaterialTheme.colorScheme.primary

  Canvas(modifier = Modifier.size(width = 452.dp, height = 200.dp).padding(top = 20.dp)) {
    val horizontalSpacing = ((size.width - 64.dp.toPx()) / (salesData.size - 1).toFloat())
    val verticalStep = size.height / 5

    drawLine(
      color = Color.Gray,
      start = Offset(36.dp.toPx(), size.height - size.height - 16),
      end = Offset(36.dp.toPx(), size.height),
      strokeWidth = 1.dp.toPx()
    )

    for (i in 0..5) {
      val y = size.height - i * verticalStep
      drawLine(
        color = Color.LightGray,
        start = Offset(36.dp.toPx(), y),
        end = Offset(size.width, y),
        strokeWidth = 1.dp.toPx()
      )
      drawContext.canvas.nativeCanvas.drawText(
        "${String.format("%d", (maxSales * i / 5).roundToInt())}",
        32.dp.toPx(),
        y + 6.dp.toPx(),
        android.graphics.Paint().apply {
          color = android.graphics.Color.BLACK
          textAlign = android.graphics.Paint.Align.RIGHT
          textSize = 36f
        }
      )
    }

    salesData.forEachIndexed { index, value ->
      val startX = 44.dp.toPx() + index * horizontalSpacing
      val startY = size.height - (value / maxSales).toFloat() * size.height

      drawLine(
        color = buttonColor,
        start = Offset(startX, size.height),
        end = Offset(startX, startY),
        strokeWidth = 6.dp.toPx()
      )
    }

    months.forEachIndexed { index, month ->
      val x = 44.dp.toPx() + index * horizontalSpacing
      val y = size.height + 20.dp.toPx()
      drawContext.canvas.nativeCanvas.drawText(
        month,
        x,
        y,
        android.graphics.Paint().apply {
          color = android.graphics.Color.BLACK
          textAlign = android.graphics.Paint.Align.CENTER
          textSize = 36f
        }
      )
    }
  }
}
