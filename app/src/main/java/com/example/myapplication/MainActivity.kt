package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.api.dto.user.LoginDto
import com.example.myapplication.api.dto.user.RegisterDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.TokenManager
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Стан для форми та токену
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var errorState by remember { mutableStateOf<String?>(null) }
                var isLoading by remember { mutableStateOf(false) }
                var token by remember { mutableStateOf<String?>(TokenManager.getToken(applicationContext)) } // Токен
                val scope = rememberCoroutineScope()

                // Перевірка на співпадіння паролів
                val passwordsMatch = password == confirmPassword

                // Логіка для реєстрації або входу
                fun registerOrLogin() {
                    scope.launch {
                        isLoading = true
                        val userService = NetworkModule.getUserService(applicationContext)

                        // Перевірка чи паролі співпадають
                        if (!passwordsMatch) {
                            errorState = "Passwords do not match"
                            isLoading = false
                            return@launch
                        }

                        try {
                            // Спроба реєстрації
                            val registerDto = RegisterDto(email = email, password = password)
                            userService.registerUser(registerDto) // Реєструємо користувача
                            Toast.makeText(applicationContext, "User registered successfully", Toast.LENGTH_SHORT).show()

                            // Після успішної реєстрації виконуємо логін
                            val loginDto = LoginDto(email = email, password = password)
                            val loggedInUser = userService.loginUser(loginDto) // Логін користувача
                            TokenManager.saveToken(applicationContext, loggedInUser.token) // Зберігаємо токен
                            token = loggedInUser.token // Оновлюємо стан токену
                            Toast.makeText(applicationContext, "Logged in successfully", Toast.LENGTH_SHORT).show()

                            // Скидаємо errorState після успішної операції
                            errorState = null

                        } catch (e: Exception) {
                            // Якщо сталася помилка, перевіряємо код помилки
                            if (e is retrofit2.HttpException && e.code() == 409) {
                                // Якщо email вже існує (код 409), пробуємо виконати логін
                                try {
                                    val loginDto = LoginDto(email = email, password = password)
                                    val loggedInUser = userService.loginUser(loginDto) // Логін користувача
                                    TokenManager.saveToken(applicationContext, loggedInUser.token) // Зберігаємо токен
                                    token = loggedInUser.token // Оновлюємо стан токену
                                    Toast.makeText(applicationContext, "Logged in successfully", Toast.LENGTH_SHORT).show()

                                    // Скидаємо errorState після успішного логіну
                                    errorState = null
                                } catch (loginException: Exception) {
                                    // Якщо не вдалося залогінитись
                                    if (loginException is retrofit2.HttpException && loginException.code() == 401) {
                                        errorState = "Invalid credentials: ${loginException.message()}"
                                    } else {
                                        errorState = "Login failed: ${loginException.message}"
                                    }
                                    Log.e("AuthError", "Login failed", loginException)
                                }
                            } else {
                                // Якщо помилка не з кодом 409
                                errorState = "Error: ${e.message}"
                                Log.e("AuthError", "Error during register or login", e)
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                }

                // Логіка для виходу
                fun logout() {
                    TokenManager.removeToken(applicationContext) // Видаляємо токен
                    token = null // Оновлюємо стан токену
                    Toast.makeText(applicationContext, "Logged out successfully", Toast.LENGTH_SHORT).show()
                }

                // Відображення інтерфейсу
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            // Якщо є токен, показуємо кнопку "Вийти"
                            if (token != null) {
                                Button(
                                    onClick = { logout() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isLoading
                                ) {
                                    Text("Logout")
                                }
                            } else {
                                // Якщо токен відсутній, показуємо форму для реєстрації/логіну
                                TextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isLoading
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                TextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    label = { Text("Password") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    enabled = !isLoading
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                TextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    label = { Text("Confirm Password") },
                                    modifier = Modifier.fillMaxWidth(),
                                    visualTransformation = PasswordVisualTransformation(),
                                    singleLine = true,
                                    enabled = !isLoading
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { registerOrLogin() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !isLoading
                                ) {
                                    Text("Register / Login")
                                }
                            }

                            // Показуємо індикатор завантаження
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                            }

                            // Показуємо помилку, якщо є
                            errorState?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Error: $it", color = MaterialTheme.colorScheme.error)
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
        MainActivity()
    }
}
