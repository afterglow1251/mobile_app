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
fun CrmMainScreen(
  onBack: () -> Unit,
  // onNavigateToClients: () -> Unit,
  navigateToCrmStats: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Система CRM") },
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
      text = "Активних клієнтів: 3",
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    Text(
      text = "Обсяг продажів: 33000 грн",
      style = MaterialTheme.typography.bodyMedium,
      modifier = Modifier.padding(bottom = 16.dp)
    )

    SalesChart()

    Spacer(modifier = Modifier.height(32.dp))

    Button(
      onClick = onBack,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Перейти до клієнтів")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
      onClick = navigateToCrmStats,
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Перейти до статистики")
    }
  }
}}

@Composable
fun SalesChart() {
  val salesData = listOf(1000, 1200, 900, 1500, 1800, 1100, 1700)
  val days = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд")

  val buttonColor = MaterialTheme.colorScheme.primary

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(220.dp)
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = "Графік продажів за останній тиждень",
      style = MaterialTheme.typography.titleMedium,
      modifier = Modifier.padding(bottom = 16.dp)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
      val maxSales = salesData.maxOrNull() ?: 1
      val horizontalSpacing = (size.width - 48.dp.toPx()) / (salesData.size - 1) // Adjusted spacing
      val offsetStart = 32.dp.toPx() // Increased offset from the vertical line
      val verticalStep = size.height / 5

      // Draw vertical scale
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
            textSize = 32f
          }
        )
      }

      // Draw line chart
      for (i in 0 until salesData.size - 1) {
        val startX = offsetStart + i * horizontalSpacing
        val startY = size.height - (salesData[i].toFloat() / maxSales) * size.height
        val endX = offsetStart + (i + 1) * horizontalSpacing
        val endY = size.height - (salesData[i + 1].toFloat() / maxSales) * size.height

        drawLine(
          color = buttonColor,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 4.dp.toPx()
        )

        // Draw points
        drawCircle(
          color = buttonColor,
          center = Offset(startX, startY),
          radius = 6.dp.toPx()
        )

        if (i == salesData.size - 2) {
          drawCircle(
            color = buttonColor,
            center = Offset(endX, endY),
            radius = 6.dp.toPx()
          )
        }
      }

      // Draw horizontal labels
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








