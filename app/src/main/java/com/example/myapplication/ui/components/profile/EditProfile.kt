package com.example.myapplication.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.LocalStorage
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.api.dto.user.UpdateDto
import com.example.myapplication.validators.isValidPhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun EditProfileScreen(
  onBack: () -> Unit
) {
  val context = LocalContext.current
  val userService = NetworkModule.getUserService(context)

  var username by remember { mutableStateOf("") }
  var phoneNumber by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("") }

  var isPhoneValid by remember { mutableStateOf(true) }
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val snackbarHostState = remember { SnackbarHostState() }
  var successMessage by remember { mutableStateOf<String?>(null) }
  var isDataLoading by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    // Завантаження початкових даних з бекенду
    try {
      val user = userService.getUserProfile() // Припускаємо, що є метод для отримання профілю
      username = user.username ?: ""
      phoneNumber = user.phoneNumber ?: ""
      address = user.address ?: ""
    } catch (e: Exception) {
      errorMessage = "Помилка завантаження даних: ${e.localizedMessage}"
    } finally {
      isDataLoading = false
    }
  }

  LaunchedEffect(successMessage) {
    if (successMessage != null) {
      delay(5000)
      successMessage = null
    }
  }

  if (isDataLoading) {
    // Екран завантаження
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  } else {
    // Основний інтерфейс
    Scaffold(
      snackbarHost = { SnackbarHost(snackbarHostState) },
      modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
      ) {
        IconButton(onClick = onBack) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Назад")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (successMessage != null) {
          Text(
            text = successMessage!!,
            color = Color.Green,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFE8F5E9))
              .padding(8.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
          text = "Редагувати профіль",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Ім'я користувача") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = phoneNumber,
          onValueChange = {
            phoneNumber = it
            isPhoneValid = it.isEmpty() || isValidPhoneNumber(it)
          },
          label = { Text("Номер телефону") },
          isError = !isPhoneValid,
          modifier = Modifier.fillMaxWidth()
        )

        if (!isPhoneValid) {
          Text(
            text = "Введіть коректний номер телефону (10-15 цифр або залиште поле порожнім).",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Адреса") },
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
          Text(
            text = errorMessage!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
          onClick = {
            if (isPhoneValid) {
              isLoading = true
              errorMessage = null
              successMessage = null

              CoroutineScope(Dispatchers.IO).launch {
                try {
                  val updateDto = UpdateDto(
                    username = username,
                    phoneNumber = phoneNumber,
                    address = address
                  )
                  val updatedUser = userService.updateProfile(updateDto)
                  withContext(Dispatchers.Main) {
                    LocalStorage.saveUser(context, updatedUser)
                    isLoading = false
                    successMessage = "Дані успішно збережені"
                  }
                } catch (e: Exception) {
                  withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Помилка оновлення: ${e.localizedMessage}"
                  }
                }
              }
            } else {
              errorMessage = "Неможливо зберегти. Перевірте номер телефону."
            }
          },
          enabled = !isLoading,
          modifier = Modifier.fillMaxWidth()
        ) {
          if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
          } else {
            Text("Зберегти")
          }
        }
      }
    }
  }
}
