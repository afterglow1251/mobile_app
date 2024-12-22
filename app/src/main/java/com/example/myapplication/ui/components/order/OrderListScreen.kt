package com.example.myapplication.ui.components.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.order.GetOrdersResponse
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(onBack: () -> Unit, cartDetails: (Int) -> Unit, showMain: () -> Unit) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  // Стан для замовлень
  var orders by remember { mutableStateOf<List<GetOrdersResponse>>(emptyList()) }
  var isLoading by remember { mutableStateOf(false) }

  // Завантаження списку замовлень через запит
  LaunchedEffect(Unit) {
    isLoading = true
    try {
      val productService = NetworkModule.getProductService(context)

      val fetchedOrders = withContext(Dispatchers.IO) {
        productService.getOrders()
      }
      orders = fetchedOrders
    } catch (e: Exception) {
      scope.launch { snackbarHostState.showSnackbar("Сталася помилка: ${e.localizedMessage}") }
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Мої замовлення") }
      )
    },
    bottomBar = {
      NavigationBar {
        NavigationBarItem(
          icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Продукти") },
          label = { Text("Продукти") },
          selected = false,
          onClick = { showMain() }
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Кошик") },
          label = { Text("Кошик") },
          selected = false,
          onClick = { LocalStorage.getUser(context)?.let { cartDetails(it.id) } }
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Person, contentDescription = "Замовлення") },
          label = { Text("Замовлення") },
          selected = true,
          onClick = {}
        )
      }
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        if (isLoading) {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            itemsIndexed(orders) { index, order ->
              OrderCard(order = order, index = index)
            }
          }
        }
      }
    }
  )
}

@Composable
fun OrderCard(order: GetOrdersResponse, index: Int) {
  val dateFormatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
  val formattedDate = try {
    val utcDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }
    val date = utcDateFormatter.parse(order.createdAt)

    // Конвертуємо дату в київський час
    dateFormatter.timeZone = TimeZone.getTimeZone("Europe/Kyiv")
    dateFormatter.format(date)
  } catch (e: Exception) {
    "Невідома дата"
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
      .padding(16.dp)
  ) {
    Text(
      text = "Замовлення №${index + 1}",
      fontSize = 18.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
      text = "Дата створення: $formattedDate",
      style = MaterialTheme.typography.bodyMedium,
      color = Color.Gray,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = "Загальна сума: ${order.totalPrice} грн.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
      text = "Адреса доставки: ${order.shippingAddress}",
      style = MaterialTheme.typography.bodyMedium,
      color = Color.Gray,
      modifier = Modifier.padding(bottom = 4.dp)
    )

    Column(modifier = Modifier.padding(top = 8.dp)) {
      Text(
        text = "Товари:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 4.dp)
      )
      order.orderItems.forEach { item ->
        Text(
          text = "- ${item.product.name} (x${item.quantity}): ${item.price} грн.",
          style = MaterialTheme.typography.bodyMedium,
          modifier = Modifier.padding(bottom = 4.dp)
        )
      }
    }
  }
}
