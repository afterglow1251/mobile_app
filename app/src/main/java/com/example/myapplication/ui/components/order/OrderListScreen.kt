package com.example.myapplication.ui.components.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
fun OrderListScreen(
  cartDetails: (Int) -> Unit, showMain: () -> Unit, onShowOrderDetails: (Int) -> Unit
) {
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  var orders by remember { mutableStateOf<List<GetOrdersResponse>>(emptyList()) }
  var isLoading by remember { mutableStateOf(false) }

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

  Scaffold(topBar = {
    TopAppBar(title = { Text("Мої замовлення") })
  }, bottomBar = {
    NavigationBar {
      NavigationBarItem(icon = {
        Icon(
          Icons.Default.ShoppingCart,
          contentDescription = "Продукти"
        )
      },
        label = { Text("Продукти") },
        selected = false,
        onClick = { showMain() })
      NavigationBarItem(icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Кошик") },
        label = { Text("Кошик") },
        selected = false,
        onClick = { LocalStorage.getUser(context)?.let { cartDetails(it.id) } })
      NavigationBarItem(icon = { Icon(Icons.Default.Person, contentDescription = "Замовлення") },
        label = { Text("Замовлення") },
        selected = true,
        onClick = {})
    }
  }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, content = { innerPadding ->
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
          val groupedOrders = orders.groupBy { order ->
            SimpleDateFormat("EEEE, dd MMMM", Locale("uk")).format(SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                Locale.getDefault()
              ).apply { timeZone = TimeZone.getTimeZone("UTC") }.parse(order.createdAt)!!)
          }

          groupedOrders.forEach { (date, ordersForDate) ->
            item {
              Text(
                text = date,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
              )
            }

            items(items = ordersForDate, key = { order -> order.id }) { order ->
              OrderSummaryCard(order = order, onShowOrderDetails = { onShowOrderDetails(order.id) })
            }
          }
        }
      }
    }
  })
}

@Composable
fun OrderSummaryCard(order: GetOrdersResponse, onShowOrderDetails: () -> Unit) {
  val dateFormatter = SimpleDateFormat("HH:mm", Locale("uk"))
  val formattedTime = try {
    val utcDateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("uk")).apply {
      timeZone = TimeZone.getTimeZone("UTC")
    }
    val date = utcDateFormatter.parse(order.createdAt)

    dateFormatter.timeZone = TimeZone.getTimeZone("Europe/Kyiv")
    dateFormatter.format(date!!)
  } catch (e: Exception) {
    "Невідомий час"
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onShowOrderDetails)
      .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
      .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "Сума: ${order.totalPrice} грн.",
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold
    )

    Text(
      text = "Час: $formattedTime", style = MaterialTheme.typography.bodyMedium, color = Color.Gray
    )
  }
}


