package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.customer.UpdateWholesaleCustomerDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.validators.isValidPhoneNumber
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

  var isPhoneValid by remember { mutableStateOf(true) }
  var isNameValid by remember { mutableStateOf(true) }
  var isAddressValid by remember { mutableStateOf(true) }
  var isLoading by remember { mutableStateOf(false) }

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

  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Редагувати клієнта", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp)
      ) {
        CompositionLocalProvider(
          LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Color(0xFF583E23),        // Колір крапельки
            backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
          )
        ) { OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            isNameValid = it.isNotEmpty()
          },
          label = { Text("Ім'я клієнта") },
          isError = !isNameValid,
          modifier = Modifier.fillMaxWidth(),colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
            errorBorderColor = Color.Red,            // Колір бордюру при помилці
            cursorColor = Color(0xFF583E23),         // Колір курсора
            focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
            unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
            errorLabelColor = Color.Red,          // Колір підказки (placeholder)
          ),
        )}
        if (!isNameValid) {
          Text(
            text = "Ім'я не може бути порожнім",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompositionLocalProvider(
          LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Color(0xFF583E23),        // Колір крапельки
            backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
          )
        ) { OutlinedTextField(
          value = phoneNumber,
          onValueChange = {
            phoneNumber = it
            isPhoneValid = isValidPhoneNumber(it)
          },
          label = { Text("Номер телефону клієнта") },
          isError = !isPhoneValid,
          modifier = Modifier.fillMaxWidth(),colors = TextFieldDefaults.outlinedTextFieldColors(
                  focusedBorderColor = Color(0xFF583E23),
          unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
          errorBorderColor = Color.Red,            // Колір бордюру при помилці
          cursorColor = Color(0xFF583E23),         // Колір курсора
          focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
          unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
          errorLabelColor = Color.Red,          // Колір підказки (placeholder)
        ),
        )}
        if (!isPhoneValid) {
          Text(
            text = "Введіть коректний номер телефону (має починатися з +38)",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        CompositionLocalProvider(
          LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Color(0xFF583E23),        // Колір крапельки
            backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
          )
        ) {
          OutlinedTextField(
            value = address,
            onValueChange = {
              address = it
              isAddressValid = it.isNotEmpty()
            },
            label = { Text("Адреса клієнта") },
            isError = !isAddressValid,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Color(0xFF583E23),
              unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
              errorBorderColor = Color.Red,            // Колір бордюру при помилці
              cursorColor = Color(0xFF583E23),         // Колір курсора
              focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
              unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
              errorLabelColor = Color.Red              // Колір мітки при помилці
            )
          )
        }
        if (!isAddressValid) {
          Text(
            text = "Адреса не може бути порожньою",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            isLoading = true
            errorMessage = null
            coroutineScope.launch {
              try {
                NetworkModule.getWholesaleCustomerService(context)
                  .updateCustomer(
                    clientId,
                    UpdateWholesaleCustomerDto(
                      name = name,
                      address = address,
                      phoneNumber = phoneNumber
                    )
                  )
                onBack()
              } catch (e: Exception) {
                errorMessage = "Не вдалося зберегти дані: ${e.localizedMessage}"
              } finally {
                isLoading = false
              }
            }
          },
          enabled = isPhoneValid && isNameValid && isAddressValid && !isLoading,
          modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF583E23), // Колір фону кнопки
            contentColor = Color.White         // Колір тексту кнопки
          ),
          shape = RoundedCornerShape(4.dp),
        ) {
          if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
          } else {
            Text("Зберегти")
          }
        }
      }
    }
  )
}