package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*

@Composable
fun ProductListScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    Text(
      text = "СПИСОК ПРОДУКТІВ",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )

    val products = listOf("Продукт 1", "Продукт 2", "Продукт 3", "Продукт 4")

    LazyColumn(
      modifier = Modifier.fillMaxSize()
    ) {
      itemsIndexed(products) { _, product ->
        Text(
          text = product,
          style = MaterialTheme.typography.bodyLarge,
          modifier = Modifier.padding(8.dp)
        )
      }
    }
  }
}

