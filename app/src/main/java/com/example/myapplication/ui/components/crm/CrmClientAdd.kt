package com.example.myapplication.ui.components.crm

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.wholesale.customer.CreateWholesaleCustomerDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.validators.isValidPhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmClientAdd(onBack: () -> Unit) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("+38") }
  var address by remember { mutableStateOf("") }
  val snackbarHostState = remember { SnackbarHostState() }
  var isPhoneValid by remember { mutableStateOf(true) }

  val context = LocalContext.current

  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Додати оптового клієнта", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
          handleColor = Color(0xFF583E23),        // Колір крапельки
          backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
        )
      ) {OutlinedTextField(
        value = name,
        onValueChange = {
          name = it
        },
        label = { Text("Введіть ім'я/назву клієнта") },
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

      Spacer(modifier = Modifier.height(8.dp))

      CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
          handleColor = Color(0xFF583E23),        // Колір крапельки
          backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
        )
      ) {OutlinedTextField(
        value = phone,
        onValueChange = {
          if (it.startsWith("+38")) {
            phone = it
          }
          isPhoneValid = it.isEmpty() || isValidPhoneNumber(it)
        },
        label = { Text("Введіть номер телефону клієнта") },
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
          style = MaterialTheme.typography.bodySmall
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
          handleColor = Color(0xFF583E23),        // Колір крапельки
          backgroundColor = Color(0xFFFFEBCD)    // Колір виділення
        )
      ) {OutlinedTextField(
        value = address,
        onValueChange = {
          address = it
        },
        label = { Text("Введіть адресу клієнта") },
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

      Spacer(modifier = Modifier.height(16.dp))

      Button(
        onClick = {
          addCustomer(context, name, phone, address, onBack, snackbarHostState)
        },
        modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp),
      ) {
        Text("Додати оптового клієнта")
      }
    }
  }
}

fun addCustomer(
  context: Context,
  name: String,
  phone: String,
  address: String,
  onBack: () -> Unit,
  snackbarHostState: SnackbarHostState
) {
  val service = NetworkModule.getWholesaleCustomerService(context)
  val createDto = CreateWholesaleCustomerDto(name, address, phone)
  CoroutineScope(Dispatchers.IO).launch {
    try {
      service.createCustomer(createDto)
      CoroutineScope(Dispatchers.Main).launch {
        onBack() // Виклик onBack одразу після успішного створення
      }
    } catch (e: Exception) {
      CoroutineScope(Dispatchers.Main).launch {
        val message = if (e.message?.contains("A customer with this phone number already exists") == true) {
          "Клієнт із таким номером телефону вже існує"
        } else {
          "Помилка: ${e.localizedMessage}"
        }
        snackbarHostState.showSnackbar(message)
      }
    }
  }
}

