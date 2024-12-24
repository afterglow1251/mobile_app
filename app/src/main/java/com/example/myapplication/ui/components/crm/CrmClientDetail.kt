package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmClientDetails(
  onBack: () -> Unit,
  // onAddOrder: (() -> Unit)?
  navigateCrmOrderList: () -> Unit,
  navigateCrmOrderDetails: () -> Unit,
  navigateCrmOrderAdd: () -> Unit
) {
  val orders = remember {
    listOf(
      Order("Замовлення #001", 1500, "Київ, вул. Хрещатик, 10", "+380501234567"),
      Order("Замовлення #002", 7000, "Львів, пл. Ринок, 1", "+380671112233"),
      Order("Замовлення #003", 12000, "Одеса, вул. Дерибасівська, 5", "+380931234567")
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Клієнт 1") },
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
        .padding(start = 16.dp, end = 16.dp)
        .verticalScroll(rememberScrollState())
    ) {

      Text(
        text = "Ім'я: Клієнт 1",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(4.dp)
      )

      Text(
        text = "Адреса: Адреса контакта 1",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(4.dp)
      )

      Text(
        text = "Телефон: телефон контакта 1",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(4.dp)
      )

      Button(

        onClick = onBack,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 4.dp)
      ) {
        Text("Редагувати інформацію про клієнта")
      }

      Button(
        onClick = navigateCrmOrderAdd,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp)
      ) {
        Text("Додати замовлення")
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clickable(onClick = navigateCrmOrderList)
          .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
      ) {
        Text(
          text = "Останні замовлення клієнта: ",
          style = MaterialTheme.typography.titleMedium,
        )
      }

      orders.forEach { order ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { navigateCrmOrderDetails() }
            .background(
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
        ) {
          Column {
            Text(
              text = order.number,
              style = MaterialTheme.typography.titleSmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Сума замовлення: ${order.totalSum} грн",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Адреса: ${order.address}",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Контактний номер: ${order.contactNumber}",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }

      Button(

        onClick = onBack,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp)
      ) {
        Text("Видалити клієнта")
      }

    }
  }
}

data class Order(
  val number: String,
  val totalSum: Int,
  val address: String,
  val contactNumber: String
)

