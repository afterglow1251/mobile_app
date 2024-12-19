package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.user.LoginDto
import com.example.myapplication.api.dto.user.RegisterDto
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Стан для форми
                var email by remember { mutableStateOf("") }
                var emailError by remember { mutableStateOf<String?>(null) }
                var isEmailChecked by remember { mutableStateOf(false) }
                var userExists by remember { mutableStateOf(false) }
                var password by remember { mutableStateOf("") }
                var confirmPassword by remember { mutableStateOf("") }
                var passwordVisible by remember { mutableStateOf(false) }
                var confirmPasswordVisible by remember { mutableStateOf(false) }
                var passwordError by remember { mutableStateOf<String?>(null) }

                // Перевірка валідності email
                fun isValidEmail(email: String): Boolean {
                    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                }

                // Відображення інтерфейсу
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Логотип
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(100.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Привітання
                            Text(
                                text = "Вітаємо у Пивному Чемпіоні",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            // Початкове поле для вводу email
                            if (!isEmailChecked) {
                                OutlinedTextField(
                                    value = email,
                                    onValueChange = {
                                        email = it
                                        emailError = if (isValidEmail(it)) null else "Некоректний формат пошти"
                                    },
                                    label = { Text("Введіть вашу пошту") },
                                    modifier = Modifier.fillMaxWidth(),
                                    isError = emailError != null
                                )

                                emailError?.let {
                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.align(Alignment.Start)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = {
                                        if (emailError == null) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val service = NetworkModule.getUserService(applicationContext)
                                                    val result = try {
                                                        service.getUserByEmail(email)
                                                    } catch (e: retrofit2.HttpException) {
                                                        if (e.code() == 400) {
                                                            null // User not found
                                                        } else {
                                                            throw e
                                                        }
                                                    }
                                                    isEmailChecked = true
                                                    userExists = result != null
                                                } catch (e: Exception) {
                                                    isEmailChecked = true // Ensure UI updates to show result
                                                    userExists = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = emailError == null
                                ) {
                                    Text("Далі")
                                }
                            } else {
                                IconButton(
                                    onClick = { isEmailChecked = false },
                                    modifier = Modifier.align(Alignment.Start)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Назад до пошти"
                                    )
                                }

                                if (userExists) {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = { password = it },
                                        label = { Text("Пароль") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                    contentDescription = if (passwordVisible) "Приховати пароль" else "Показати пароль"
                                                )
                                            }
                                        },
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val service = NetworkModule.getUserService(applicationContext)
                                                    service.loginUser(LoginDto(email = email, password = password))
                                                } catch (e: retrofit2.HttpException) {
                                                    if (e.code() == 401) {
                                                    } else {
                                                        throw e
                                                    }
                                                } catch (e: Exception) {
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Увійти")
                                    }
                                } else {
                                    OutlinedTextField(
                                        value = password,
                                        onValueChange = {
                                            password = it
                                            passwordError = if (password == confirmPassword) null else "Паролі не співпадають"
                                        },
                                        label = { Text("Пароль") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                                Icon(
                                                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                    contentDescription = if (passwordVisible) "Приховати пароль" else "Показати пароль"
                                                )
                                            }
                                        },
                                        isError = passwordError != null,
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = confirmPassword,
                                        onValueChange = {
                                            confirmPassword = it
                                            passwordError = if (password == confirmPassword) null else "Паролі не співпадають"
                                        },
                                        label = { Text("Підтвердіть пароль") },
                                        modifier = Modifier.fillMaxWidth(),
                                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        trailingIcon = {
                                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                                Icon(
                                                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                                    contentDescription = if (confirmPasswordVisible) "Приховати пароль" else "Показати пароль"
                                                )
                                            }
                                        },
                                        isError = passwordError != null,
                                        singleLine = true
                                    )

                                    passwordError?.let {
                                        Text(
                                            text = it,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.align(Alignment.Start)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    val service = NetworkModule.getUserService(applicationContext)
                                                    service.registerUser(RegisterDto(email = email, password = password))

                                                    service.loginUser(LoginDto(email = email, password = password))
                                                } catch (e: Exception) {
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        enabled = passwordError == null
                                    ) {
                                        Text("Зареєструватись")
                                    }
                                }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Preview of Initial Screen", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

