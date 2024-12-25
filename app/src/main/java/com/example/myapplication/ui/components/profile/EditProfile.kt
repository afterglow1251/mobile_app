package com.example.myapplication.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.utils.LocalStorage
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.api.dto.user.UpdateDto
import com.example.myapplication.validators.isValidPhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
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
  var isDataLoading by remember { mutableStateOf(true) }

  LaunchedEffect(Unit) {
    try {
      val user = userService.getUserProfile()
      username = user.username ?: ""
      phoneNumber = user.phoneNumber ?: ""
      address = user.address ?: ""
    } catch (e: Exception) {
      errorMessage = "Помилка завантаження даних: ${e.localizedMessage}"
    } finally {
      isDataLoading = false
    }
  }

  if (isDataLoading) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }
  } else {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("Редагувати профіль") },
          navigationIcon = {
            IconButton(onClick = onBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
          }
        )
      }
    ) { paddingValues ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
      ) {
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
            text = "Введіть коректний номер телефону (має починатися з +38)",
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
            isLoading = true
            errorMessage = null

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
                  onBack()
                }
              } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                  isLoading = false
                  errorMessage = "Помилка оновлення: ${e.localizedMessage}"
                }
              }
            }
          },
          enabled = isPhoneValid && phoneNumber.isNotEmpty() && username.isNotEmpty() && !isLoading,
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