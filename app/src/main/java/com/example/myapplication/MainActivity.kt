package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.myapplication.api.dto.user.LoginDto
import com.example.myapplication.api.dto.user.UserDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.TokenManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Створення станів для email та пароля
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var errorState by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }
                var usersList by remember { mutableStateOf<List<UserDto>?>(null) }
                var userProfile by remember { mutableStateOf<UserDto?>(null) }  // Стан для профілю
                val scope = rememberCoroutineScope()

                // Отримуємо контролер клавіатури
                val keyboardController = LocalSoftwareKeyboardController.current

                // Функція для логіну
                fun login() {
                    scope.launch {
                        try {
                            isLoading = true
                            val userService = NetworkModule.getUserService(applicationContext)

                            // Створюємо LoginDto замість UserDto
                            val loginDto = LoginDto(email = email, password = password)

                            // Викликаємо API для логіну
                            val loggedInUser = userService.loginUser(loginDto)

                            TokenManager.saveToken(applicationContext, loggedInUser.token)

                            // При успішному логіні, зберігаємо токен та продовжуємо роботу
                            Toast.makeText(applicationContext, "Welcome, ${loggedInUser.token}!", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            errorState = "Error: ${e.message}"
                            Log.e("Login", "Error during login", e)
                        } finally {
                            isLoading = false
                        }
                    }
                }

                // Функція для отримання користувачів
                fun getUsers() {
                    val token = TokenManager.getToken(applicationContext) // Отримуємо токен

                    if (token != null) {
                        scope.launch {
                            try {
                                val userService = NetworkModule.getUserService(applicationContext)
                                val users = userService.getUsers(authHeader = "Bearer $token")
                                usersList = users
                            } catch (e: Exception) {
                                errorState = "Error: ${e.message}"
                                Log.e("GetUsers", "Error during get users", e)
                            }
                        }
                    } else {
                        errorState = "Token not found. Please log in first."
                        Log.e("GetUsers", "Token not found")
                    }
                }

                // Функція для отримання профілю користувача
                fun getProfile() {
                    val token = TokenManager.getToken(applicationContext) // Отримуємо токен

                    if (token != null) {
                        scope.launch {
                            try {
                                val userService = NetworkModule.getUserService(applicationContext)
                                val profile = userService.getUserProfile(authHeader = "Bearer $token")
                                userProfile = profile // Зберігаємо профіль у стані
                            } catch (e: Exception) {
                                errorState = "Error: ${e.message}"
                                Log.e("GetProfile", "Error during get profile", e)
                            }
                        }
                    } else {
                        errorState = "Token not found. Please log in first."
                        Log.e("GetProfile", "Token not found")
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Додаємо клік для приховування клавіатури при натисканні на порожнє місце
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                keyboardController?.hide() // При натисканні приховуємо клавіатуру
                            }
                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            // Форма для логіну
                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Email") },
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Password") },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Кнопка логіну
                            Button(
                                onClick = { login() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text("Login")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Кнопка для отримання користувачів
                            Button(
                                onClick = { getUsers() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text("Get Users")
                            }

                            // Кнопка для отримання профілю
                            Button(
                                onClick = { getProfile() },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isLoading
                            ) {
                                Text("Get Profile")
                            }

                            // Показуємо статус
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                            }

                            errorState?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Error: $it", color = MaterialTheme.colorScheme.error)
                            }

                            // Показуємо список користувачів
                            usersList?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                it.forEach { user ->
                                    Text("User: ${user.username} - ${user.email}")
                                }
                            }

                            // Показуємо профіль користувача
                            userProfile?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Profile Username: ${it.username}")
                                Text("Profile Email: ${it.email}")
                                // Додаткові поля профілю
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MyApplicationTheme {
        // Попередній перегляд форми
        MainActivity()
    }
}
