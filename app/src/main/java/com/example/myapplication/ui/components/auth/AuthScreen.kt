package com.example.myapplication.ui.components.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.myapplication.api.dto.user.*
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.*
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.R.*
import com.example.myapplication.validators.isValidEmail
import com.example.myapplication.validators.isValidPassword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onNavigateToProductsList: () -> Unit) {
  var email by remember { mutableStateOf("") }
  var emailError by remember { mutableStateOf<String?>(null) }
  var isEmailChecked by remember { mutableStateOf(false) }
  var userExists by remember { mutableStateOf(false) }
  var password by remember { mutableStateOf("") }
  var confirmPassword by remember { mutableStateOf("") }
  var passwordVisible by remember { mutableStateOf(false) }
  var confirmPasswordVisible by remember { mutableStateOf(false) }
  var passwordError by remember { mutableStateOf<String?>(null) }

  val context = LocalContext.current



  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = Color(0xFFFDF8ED)
  )
  { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (isEmailChecked) {
        IconButton(
          onClick = { isEmailChecked = false },
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = "Назад до пошти"
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Image(
          painter = painterResource(id = drawable.emblem1),
          contentDescription = null,
          modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "Вітаємо у Пивному Чемпіоні",
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          color = Color(0xFF583E23),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isEmailChecked) {
          CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
              handleColor = Color(0xFF583E23),
              backgroundColor = Color(0xFFFFEBCD)
            )
          ) {
          OutlinedTextField(
            value = email,
            onValueChange = {
              email = it
              emailError = if (email.isBlank()) "Поле не може бути порожнім" else if (isValidEmail(it)) null else "Некоректний формат пошти"
            },
            label = { Text("Введіть вашу пошту") },
            modifier = Modifier.fillMaxWidth(),
            isError = emailError != null,
            colors = TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Color(0xFF583E23),
              unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
              errorBorderColor = Color.Red,            // Колір бордюру при помилці
              cursorColor = Color(0xFF583E23),         // Колір курсора
              focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
              unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
              errorLabelColor = Color.Red,          // Колір підказки (placeholder)
            )
          )}

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
              if (emailError == null && email.isNotBlank()) {
                CoroutineScope(Dispatchers.IO).launch {
                  try {
                    val service = NetworkModule.getUserService(context)
                    val result = try {
                      service.getUserByEmail(email)
                    } catch (e: retrofit2.HttpException) {
                      if (e.code() == 400) null else throw e
                    }
                    isEmailChecked = true
                    userExists = result != null
                  } catch (e: Exception) {
                    isEmailChecked = true
                    userExists = false
                  }
                }
              }
            },
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF583E23), // Колір фону кнопки
              contentColor = Color.White         // Колір тексту кнопки
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            enabled = emailError == null && email.isNotBlank()
          ) {
            Text("Далі")
          }
        } else {
          if (userExists) {
            CompositionLocalProvider(
              LocalTextSelectionColors provides TextSelectionColors(
                handleColor = Color(0xFF583E23),
                backgroundColor = Color(0xFFFFEBCD)
              )
            ) { OutlinedTextField(
              value = password,
              onValueChange = {
                password = it
                passwordError = null
              },
              label = { Text("Пароль") },
              colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF583E23),
                unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
                errorBorderColor = Color.Red,            // Колір бордюру при помилці
                cursorColor = Color(0xFF583E23),         // Колір курсора
                focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
                unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
                errorLabelColor = Color.Red,          // Колір підказки (placeholder)
              ),
              modifier = Modifier.fillMaxWidth(),
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                  )
                }
              },
              isError = passwordError != null
            )}

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
                    val service = NetworkModule.getUserService(context)
                    val loginResponse = service.loginUser(LoginDto(email, password))
                    LocalStorage.saveToken(context, loginResponse.token)

                    LocalStorage.saveUser(
                      context, UserDto(
                        id = loginResponse.user.id,
                        email = loginResponse.user.email,
                        username = loginResponse.user.username,
                        phoneNumber = loginResponse.user.phoneNumber,
                        address = loginResponse.user.address,
                        isEmployee = loginResponse.user.isEmployee,
                      )
                    )

                    withContext(Dispatchers.Main) {
                      onNavigateToProductsList()
                    }

                  } catch (e: retrofit2.HttpException) {
                    if (e.code() == 401) {
                      passwordError = "Невірний емейл або пароль"
                    } else {
                      throw e
                    }
                  } catch (e: Exception) {
                    passwordError = "Сталася помилка: ${e.localizedMessage}"
                  }
                }
              },
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(4.dp),
                      colors = ButtonDefaults.buttonColors(
                      containerColor = Color(0xFF583E23), // Колір фону кнопки
              contentColor = Color.White         // Колір тексту кнопки
            ),
            ) {
              Text("Увійти")
            }
          } else {
            CompositionLocalProvider(
              LocalTextSelectionColors provides TextSelectionColors(
                handleColor = Color(0xFF583E23),
                backgroundColor = Color(0xFFFFEBCD)
              )
            ) {
            OutlinedTextField(
              value = password,
              onValueChange = {
                password = it
                passwordError = when {
                  !isValidPassword(it) -> "Пароль повинен містити щонайменше 6 символів"
                  it != confirmPassword -> "Паролі не співпадають"
                  else -> null
                }
              },
              colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF583E23),
                unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
                errorBorderColor = Color.Red,            // Колір бордюру при помилці
                cursorColor = Color(0xFF583E23),         // Колір курсора
                focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
                unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
                errorLabelColor = Color.Red,          // Колір підказки (placeholder)
              ),
              label = { Text("Пароль") },
              modifier = Modifier.fillMaxWidth(),
              visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                  Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                  )
                }
              },
              isError = passwordError != null
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
              value = confirmPassword,
              onValueChange = {
                confirmPassword = it
                passwordError = when {
                  password != it -> "Паролі не співпадають"
                  !isValidPassword(password) -> "Пароль повинен містити щонайменше 6 символів"
                  else -> null
                }
              },
              label = { Text("Підтвердіть пароль") },
              modifier = Modifier.fillMaxWidth(),
              colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF583E23),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF583E23),
                focusedLabelColor = Color(0xFF583E23),
                unfocusedLabelColor = Color.Gray,
                errorLabelColor = Color.Red
              ),
              visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              trailingIcon = {
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                  Icon(
                    imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = null
                  )
                }
              },
              isError = passwordError != null
            )}

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
                    val service = NetworkModule.getUserService(context)
                    service.registerUser(RegisterDto(email = email, password = password))

                    val loginResponse = service.loginUser(LoginDto(email, password))
                    LocalStorage.saveToken(context, loginResponse.token)

                    LocalStorage.saveUser(
                      context, UserDto(
                        id = loginResponse.user.id,
                        email = loginResponse.user.email,
                        username = loginResponse.user.username,
                        phoneNumber = loginResponse.user.phoneNumber,
                        address = loginResponse.user.address,
                        isEmployee = loginResponse.user.isEmployee,
                      )
                    )

                    withContext(Dispatchers.Main) {
                      onNavigateToProductsList()
                    }

                  } catch (e: Exception) {
                    passwordError = "Сталася помилка: ${e.localizedMessage}"
                  }
                }
              },
              modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF583E23), // Колір фону кнопки
                contentColor = Color.White         // Колір тексту кнопки
              ),
              shape = RoundedCornerShape(4.dp),
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
