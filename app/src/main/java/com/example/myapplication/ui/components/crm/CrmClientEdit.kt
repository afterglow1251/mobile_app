package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.customer.UpdateWholesaleCustomerDto
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmClientEdit(
  clientId: Int,
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var name by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  // Завантаження даних клієнта
  LaunchedEffect(clientId) {
    try {
      val client = NetworkModule.getWholesaleCustomerService(context).getCustomerById(clientId)
      name = client.name
      address = client.address
      phoneNumber = client.phoneNumber
    } catch (e: Exception) {
      errorMessage = "Не вдалося завантажити дані клієнта: ${e.localizedMessage}"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Редагувати клієнта") },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        }
      )
    },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        when {
          errorMessage != null -> {
            Text(
              text = errorMessage ?: "",
              color = MaterialTheme.colorScheme.error
            )
          }
          successMessage != null -> {
            Text(
              text = successMessage ?: "",
              color = MaterialTheme.colorScheme.primary
            )
          }
          else -> {
            OutlinedTextField(
              value = name,
              onValueChange = { name = it },
              label = { Text("Ім'я клієнта") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = address,
              onValueChange = { address = it },
              label = { Text("Адреса клієнта") },
              modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
              value = phoneNumber,
              onValueChange = { phoneNumber = it },
              label = { Text("Телефон клієнта") },
              modifier = Modifier.fillMaxWidth()
            )
            Button(
              onClick = {
                coroutineScope.launch {
                  try {
                    val updatedClient = NetworkModule.getWholesaleCustomerService(context)
                      .updateCustomer(
                        clientId,
                        UpdateWholesaleCustomerDto(
                          name = name,
                          address = address,
                          phoneNumber = phoneNumber
                        )
                      )
                    successMessage = "Дані успішно оновлено!"
                    name = updatedClient.name
                    address = updatedClient.address
                    phoneNumber = updatedClient.phoneNumber
                  } catch (e: Exception) {
                    errorMessage = "Не вдалося зберегти дані: ${e.localizedMessage}"
                  }
                }
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Зберегти")
            }
          }
        }
      }
    }
  )
}