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
fun CrmArchiveMonthScreen(
  onBack: () -> Unit,
  // onMonthStatsNavigate: () -> Unit,
  // onYearStatsNavigate: () -> Unit
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
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmArchiveYearScreen(
  onBack: () -> Unit,
  // onMonthStatsNavigate: () -> Unit,
  // onYearStatsNavigate: () -> Unit
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
