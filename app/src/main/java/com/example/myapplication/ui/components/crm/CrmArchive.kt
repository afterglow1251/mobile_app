package com.example.myapplication.ui.components.crm

import android.util.Log
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
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmArchiveScreen(
  onBack: () -> Unit,
  navigateToCrmMonthArchive: () -> Unit,
  navigateToCrmYearArchive: () -> Unit
) {
  val monthsInUkrainian = listOf(
    "Січень", "Лютий", "Березень", "Квітень", "Травень",
    "Червень", "Липень", "Серпень", "Вересень", "Жовтень",
    "Листопад", "Грудень"
  )
  val context = LocalContext.current
  val customerService = NetworkModule.getWholesaleCustomerService(context)
  val coroutineScope = rememberCoroutineScope()

  var lastThreeMonthsOrders by remember { mutableStateOf<Map<String, List<WholesaleOrderDto>>>(emptyMap()) }
  var lastThreeYearsOrders by remember { mutableStateOf<Map<String, List<WholesaleOrderDto>>>(emptyMap()) }
  var isLoading by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    isLoading = true
    try {
      val allOrders = customerService.getAllCustomers().flatMap { it.orders }
      val calendar = Calendar.getInstance()

      lastThreeMonthsOrders = (0 until 3).associate { monthOffset ->
        val targetCalendar = Calendar.getInstance().apply {
          add(Calendar.MONTH, -monthOffset)
        }
        val monthIndex = targetCalendar.get(Calendar.MONTH)
        val monthName = monthsInUkrainian[monthIndex]
        val year = targetCalendar.get(Calendar.YEAR)
        val monthOrders = allOrders.filter {
          val orderDate = parseDate(it.createdAt)
          orderDate != null &&
                  targetCalendar.get(Calendar.MONTH) == Calendar.getInstance().apply { time = orderDate }.get(Calendar.MONTH) &&
                  targetCalendar.get(Calendar.YEAR) == Calendar.getInstance().apply { time = orderDate }.get(Calendar.YEAR)
        }
        "$monthName $year" to monthOrders
      }

      lastThreeYearsOrders = (0 until 3).associate { yearOffset ->
        val targetYear = calendar.get(Calendar.YEAR) - yearOffset
        val yearOrders = allOrders.filter {
          val orderDate = parseDate(it.createdAt)
          orderDate != null && Calendar.getInstance().apply { time = orderDate }.get(Calendar.YEAR) == targetYear
        }
        "$targetYear" to yearOrders
      }

    } catch (e: Exception) {
      // Handle error
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Архів статистики") },
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
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToCrmMonthArchive() }
            .padding(vertical = 8.dp)
        ) {
          Text(
            text = "Статистика за місяці",
            style = MaterialTheme.typography.titleLarge
          )
        }

        Log.d("Debug", "$lastThreeMonthsOrders")
        lastThreeMonthsOrders.forEach { (monthName, orders) ->
          Text(
            text = monthName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
          )
          Log.d("Order debug", "$orders")
          SalesChartMonthlyAny(orders = orders)

          Spacer(modifier = Modifier.height(26.dp))
        }

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToCrmYearArchive() }
            .padding(vertical = 8.dp)
        ) {
          Text(
            text = "Статистика за роки",
            style = MaterialTheme.typography.titleLarge
          )
        }

        lastThreeYearsOrders.forEach { (year, orders) ->
          Log.d("YearOrd", "${orders}")
          Text(
            text = "$year",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
          )
          SalesChartYearlyAny(orders = orders)
          Spacer(modifier = Modifier.height(26.dp))
        }
      }
    }
  }
}

@Composable
fun generateDummyDataComposable(points: Int, min: Int, max: Int): List<Int> {
  return remember { List(points) { (min..max).random() } }
}

@Composable
fun SalesChartWithCustomData(
  data: List<Int>,
  months: List<String> = listOf("Лют", "Квіт", "Черв", "Серп", "Жовт", "Груд"),
  modifier: Modifier = Modifier
) {
  val maxSales = data.maxOrNull() ?: 1

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    val buttonColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = Modifier.fillMaxSize()) {

      val horizontalSpacing = ((size.width - 64.dp.toPx()) / (months.size - 1)) / 2
      val horizontalSpacingMonth = (size.width - 64.dp.toPx()) / (months.size - 1)
      val verticalStep = size.height / 5

      drawLine(
        color = Color.Gray,
        start = Offset(16.dp.toPx(), 0f),
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
          "${maxSales * i / 5}",
          12.dp.toPx(),
          y + 6.dp.toPx(),
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.RIGHT
            textSize = 36f
          }
        )
      }

      for (i in 0 until data.size - 1) {
        val startX = 24.dp.toPx() + i * horizontalSpacing
        val startY = size.height - (data[i].toFloat() / maxSales) * size.height
        val endX = 24.dp.toPx() + (i + 1) * horizontalSpacing
        val endY = size.height - (data[i + 1].toFloat() / maxSales) * size.height

        drawLine(
          color = buttonColor,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 4.dp.toPx()
        )

        drawCircle(
          color = buttonColor,
          center = Offset(startX, startY),
          radius = 4.5.dp.toPx()
        )

        if (i == data.size - 2) {
          drawCircle(
            color = buttonColor,
            center = Offset(endX, endY),
            radius = 4.5.dp.toPx()
          )
        }
      }

      months.forEachIndexed { index, month ->
        val x = 24.dp.toPx() + index * horizontalSpacingMonth + 31.5.dp.toPx()
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
}

@Composable
fun SalesChartForDays(
  data: List<Int>,
  days: List<String> = listOf("1", "5", "10", "15", "20", "25", "30"),
  modifier: Modifier = Modifier
) {

  val buttonColor = MaterialTheme.colorScheme.primary
  val maxSales = data.maxOrNull() ?: 1

  Column(
    modifier = modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val horizontalSpacing = ((size.width - 64.dp.toPx()) / (data.size - 1)) * 1.037f
      val horizontalSpacingDays = ((size.width - 64.dp.toPx()) / (data.size - 1)) * 5
      val verticalStep = size.height / 5

      drawLine(
        color = Color.Gray,
        start = Offset(16.dp.toPx(), 0f),
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
          "${maxSales * i / 5}",
          12.dp.toPx(),
          y + 6.dp.toPx(),
          android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textAlign = android.graphics.Paint.Align.RIGHT
            textSize = 36f
          }
        )
      }

      for (i in 0 until data.size - 1) {
        val startX = 24.dp.toPx() + i * horizontalSpacing
        val startY = size.height - (data[i].toFloat() / maxSales) * size.height
        val endX = 24.dp.toPx() + (i + 1) * horizontalSpacing
        val endY = size.height - (data[i + 1].toFloat() / maxSales) * size.height

        drawLine(
          color = buttonColor,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 4.dp.toPx()
        )

        drawCircle(
          color = buttonColor,
          center = Offset(startX, startY),
          radius = 4.5.dp.toPx()
        )

        if (i == data.size - 2) {
          drawCircle(
            color = buttonColor,
            center = Offset(endX, endY),
            radius = 4.5.dp.toPx()
          )
        }
      }

      days.forEachIndexed { index, day ->
        val x = 24.dp.toPx() + index * horizontalSpacingDays
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
fun SalesChartMonthlyAny(orders: List<WholesaleOrderDto>) {
  val salesData = (1..31).map { day ->
    orders.filter { order ->
      val orderDate = parseDate(order.createdAt)
      orderDate != null &&
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
fun SalesChartYearlyAny(orders: List<WholesaleOrderDto>) {
  val salesData = (1..12).map { month ->
    orders.filter { order ->
      val orderDate = parseDate(order.createdAt)
      orderDate != null &&
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
