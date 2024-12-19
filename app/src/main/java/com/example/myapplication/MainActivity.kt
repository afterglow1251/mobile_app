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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Модель для завдання (Todo)
data class Todo(
    val userId: Int,
    val id: Int,
    val title: String,
    val completed: Boolean
)

// Інтерфейс API
interface ApiService {
    @GET("todos/1")  // Отримуємо перше завдання для тесту
    suspend fun getTodo(): Todo
}

// Налаштування Retrofit
object RetrofitInstance {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Створення стану для завдання
                val todoState = remember { mutableStateOf<Todo?>(null) }
                val errorState = remember { mutableStateOf<String?>(null) }
                val isLoading = remember { mutableStateOf(true) }

                // Логіка запиту при запуску
                LaunchedEffect(Unit) {
                    try {
                        val todo = RetrofitInstance.apiService.getTodo()
                        todoState.value = todo
                    } catch (e: Exception) {
                        errorState.value = "Error fetching todo: ${e.message}"
                        Log.e("MainActivity", "Error fetching todo", e)
                    } finally {
                        isLoading.value = false
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TodoDetails(
                        todo = todoState.value,
                        error = errorState.value,
                        isLoading = isLoading.value,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Компонент для відображення завдання
@Composable
fun TodoDetails(todo: Todo?, error: String?, isLoading: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        when {
            isLoading -> Text("Loading...", modifier = modifier)
            error != null -> Text("Error: $error", color = MaterialTheme.colorScheme.error, modifier = modifier)
            todo == null -> Text("No data available", modifier = modifier)
            else -> {
                Text(text = "Todo ID: ${todo.id}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Title: ${todo.title}", style = MaterialTheme.typography.headlineSmall)
                Text(text = "Completed: ${if (todo.completed) "Yes" else "No"}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        TodoDetails(todo = null, error = null, isLoading = false)
    }
}
