package com.example.myapplication.ui.components.crm

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
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmStatsScreen(
  onBack: () -> Unit,
   navigateToCrmArchive: () -> Unit,
) {
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
        .padding(top = 0.dp, start = 16.dp, end = 16.dp)
    ) {
      Text(
        text = "Активних клієнтів: 15",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
        text = "Обсяг продажів за весь час: 150000 грн",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
        text = "Всього замовлень: 320",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
        text = "Найактивніший клієнт: Іван Петров",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 8.dp)
      )

      Text(
        text = "Найприбутковіший клієнт: Марія Іванова",
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      Text(
        text = "Обсяг продажів за останній місяць",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      SalesChartMonthly()

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "Обсяг продажів за цей рік",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      SalesChartYearly()

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


@Composable
fun SalesChartMonthly() {
  val salesData = listOf(1000, 2000, 1500, 2500, 1800, 3000, 2300, 2000, 2700, 2200, 1900, 2300, 2600, 2000, 2000, 2100, 1000, 1400, 900, 700, 800, 700, 1200, 1800, 2300, 2600, 2400, 2500, 2400, 2700)
  val days = listOf("1", "5", "10", "15", "20", "25", "30")

  val buttonColor = MaterialTheme.colorScheme.primary

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val maxSales = salesData.maxOrNull() ?: 1
      val horizontalSpacing = ((size.width - 64.dp.toPx()) / (salesData.size - 1)) * 1.037f // Adjusted for stretching
      val horizontalSpacingDays = ((size.width - 64.dp.toPx()) / (salesData.size - 1)) * 5
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

      for (i in 0 until salesData.size - 1) {
        val startX = 24.dp.toPx() + i * horizontalSpacing // Adjusted for offset and scaling
        val startY = size.height - (salesData[i].toFloat() / maxSales) * size.height
        val endX = 24.dp.toPx() + (i + 1) * horizontalSpacing
        val endY = size.height - (salesData[i + 1].toFloat() / maxSales) * size.height

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

        if (i == salesData.size - 2) {
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
fun SalesChartYearly() {
  val salesData = listOf(10000, 20000, 30000, 40000, 25000, 35000, 45000, 50000, 55000, 60000, 70000, 80000)
  val months = listOf("Лют", "Квіт", "Черв", "Серп", "Жовт", "Груд")

  val buttonColor = MaterialTheme.colorScheme.primary

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val maxSales = salesData.maxOrNull() ?: 1
      val horizontalSpacing = ((size.width - 64.dp.toPx()) / (months.size - 1))/2
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

      for (i in 0 until salesData.size - 1) {
        val startX = 24.dp.toPx() + i * horizontalSpacing // Adjusted for offset and scaling
        val startY = size.height - (salesData[i].toFloat() / maxSales) * size.height
        val endX = 24.dp.toPx() + (i + 1) * horizontalSpacing
        val endY = size.height - (salesData[i + 1].toFloat() / maxSales) * size.height

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

        if (i == salesData.size - 2) {
          drawCircle(
            color = buttonColor,
            center = Offset(endX, endY),
            radius = 4.5.dp.toPx()
          )
        }
      }

      months.forEachIndexed { index, month ->
        val x = 24.dp.toPx() + index * horizontalSpacingMonth + 31.5.dp.toPx() // Added offset for month labels
        val y = size.height + 20.dp.toPx() // Adjusted bottom spacing
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