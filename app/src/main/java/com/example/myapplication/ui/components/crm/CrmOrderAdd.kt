package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderAdd(onBack: () -> Unit) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Додати оптового клієнта") },
        navigationIcon = {
          IconButton(onClick = onBack) {
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
        .padding(16.dp)
    ) {
      //Text(
      //  text = "Ім'я клієнта:",
      //  style = MaterialTheme.typography.bodyLarge
      //)
      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
        },
        label = { Text("Введіть ім'я/назву клієнта") },
        modifier = Modifier.fillMaxWidth()
      )

      //Text(
      //  text = "Номер телефону:",
      //  style = MaterialTheme.typography.bodyLarge
      //)

      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
        },
        label = { Text("Введіть номер телефону клієнту") },
        modifier = Modifier.fillMaxWidth()
      )

      //Text(
      //  text = "Адреса клієнта:",
      //  style = MaterialTheme.typography.bodyLarge
      //)

      OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
        },
        label = { Text("Введіть Адресу клієнту") },
        modifier = Modifier.fillMaxWidth()
      )

      Button(
        onClick = onBack,
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 16.dp)

      ) {
        Text("Додати оптового клієнта")
      }
    }
  }
}
