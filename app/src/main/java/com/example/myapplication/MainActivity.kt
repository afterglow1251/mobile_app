package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.network.RetrofitInstance
import retrofit2.http.*

// Модель для користувача (User)
data class User(
    val id: Int,
    val email: String,
    val username: String
)

// Інтерфейс API
interface ApiService {
    @GET("users")  // Отримуємо список користувачів
    suspend fun getUsers(@Header("Authorization") authHeader: String): List<User>
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Створення стану для списку користувачів
                val usersState = remember { mutableStateOf<List<User>?>(null) }
                val errorState = remember { mutableStateOf<String?>(null) }
                val isLoading = remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    try {
                        val token = "<token>"  // Тут задайте свій токен
                        // Передаємо токен через заголовок запиту
                        val users = RetrofitInstance.apiService.getUsers("Bearer $token")
                        usersState.value = users
                    } catch (e: Exception) {
                        errorState.value = "Error fetching users: ${e.message}"
                        Log.e("MainActivity", "Error fetching users", e)
                    } finally {
                        isLoading.value = false
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UsersList(
                        users = usersState.value,
                        error = errorState.value,
                        isLoading = isLoading.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Компонент для відображення списку користувачів
@Composable
fun UsersList(users: List<User>?, error: String?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        when {
            isLoading -> Text("Loading...", modifier = modifier)
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = modifier)
            users.isNullOrEmpty() -> Text("No data available", modifier = modifier)
            else -> {
                users.forEach { user ->
                    Text(text = "User ID: ${user.id}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Username: ${user.username}", style = MaterialTheme.typography.headlineSmall)
                    Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        UsersList(users = null, error = null, isLoading = false)
    }
}
