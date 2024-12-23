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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmArchiveScreen(
  onBack: () -> Unit,
  navigateToCrmMonthArchive: () -> Unit,
  navigateToCrmYearArchive: () -> Unit
) {
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
      // Monthly Statistics Section
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { navigateToCrmMonthArchive() }
          .padding(vertical = 8.dp)
      ) {
        Text(
          text = "Статистика за місяцями",
          style = MaterialTheme.typography.titleLarge
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
//          .padding(vertical = 8.dp)
      ){

        Text(
          text = "Місяць 1",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartForDays(listOf(1000, 2000, 1500, 2500, 1800, 3000, 2300, 2000, 2700, 2200, 1900, 2300, 2600, 2000, 2000, 2700, 1000, 1900, 900, 700, 800, 700, 1200, 1800, 2300, 2600, 2400, 2500, 2400, 2700))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 150000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
        Text(
          text = "Місяць 2",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartForDays(listOf(1200, 1500, 1800, 2100, 1700, 2600, 1900, 2200, 2400, 2300, 2800, 2500, 2000, 2600, 1900, 1800, 1700, 1900, 1500, 1400, 1300, 700, 1100, 1000, 900, 800, 700, 600, 500, 400))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 50000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
        Text(
          text = "Місяць 3",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartForDays(listOf(800, 1200, 1100, 1400, 1500, 1600, 1700, 1800, 2000, 1900, 2200, 2100, 2300, 2200, 2500, 2900, 1700, 2800, 2900, 3000, 5100, 5200, 5300, 3400, 3500, 3600, 3700, 3800, 3900, 4000))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 110000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Yearly Statistics Section
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

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
      ) {

        Text(
          text = "Рік 1",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartWithCustomData(intArrayOf(12000, 3000, 4000, 5000, 2000, 0, 0, 0 ,0, 5000, 9000, 0).toList())
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 150000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
        Text(
          text = "Рік 2",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartWithCustomData(intArrayOf(1000, 13000, 4500, 0, 2600, 9000, 4500, 7000 ,4000, 500, 6000, 2000).toList())
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 50000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
        Text(
          text = "Рік 3",
          style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        SalesChartWithCustomData(intArrayOf(7000, 4000, 6000, 4000, 2000, 5000, 7000, 6000, 3000, 8000, 4000, 5000).toList())
        Spacer(modifier = Modifier.height(20.dp))
        Text(
          text = "Обсяг продажів за цей місяць: 450000 грн",
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
        )
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
