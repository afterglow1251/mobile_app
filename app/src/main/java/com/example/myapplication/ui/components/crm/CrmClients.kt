import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientListScreen(
  onBack: () -> Unit,
//  onAddClient: () -> Unit
) {
  val clients = remember {
    listOf(
      Client("Іван Петров", 5, 15000, "2023-12-20"),
      Client("Марія Іванова", 3, 7000, "2023-12-18"),
      Client("Олександр Ковальчук", 10, 35000, "2023-12-19")
    )
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Список клієнтів") },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        }
      )
    }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Button(
        onClick = onBack,
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp)
      ) {
        Text("Додати оптового клієнта")
      }

      Text(
        text = "Ваші клієнти:",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 16.dp)
      )

      clients.forEach { client ->
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clickable { /* Handle client click */ }
            .background(
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
              shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
        ) {
          Column {
            Text(
              text = client.name,
              style = MaterialTheme.typography.titleSmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Кількість замовлень: ${client.orderCount}",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Сума замовлень: ${client.totalOrderSum} грн",
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
              text = "Дата останнього замовлення: ${client.lastOrderDate}",
              style = MaterialTheme.typography.bodySmall
            )
          }
        }
      }
    }
  }
}

data class Client(
  val name: String,
  val orderCount: Int,
  val totalOrderSum: Int,
  val lastOrderDate: String
)