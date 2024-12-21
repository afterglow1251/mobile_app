package com.example.myapplication.ui.components.products

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.network.NetworkModule
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.components.ui.PicassoImage
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(showProfile: () -> Unit, showProductDetails: (Int) -> Unit, cartDetails: (Int) -> Unit) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  // Стан для продуктів
  var products by remember { mutableStateOf<List<ProductDto>>(emptyList()) }
  var isLoading by remember { mutableStateOf(false) }

  // Завантаження продуктів
  LaunchedEffect(Unit) {
    isLoading = true
    try {
      val productService = NetworkModule.getProductService(context)
      products = productService.getAllProducts()
    } catch (e: Exception) {
      scope.launch { snackbarHostState.showSnackbar("Сталася помилка: ${e.localizedMessage}") }
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("СПИСОК ПРОДУКТІВ") },
        actions = {
          IconButton(onClick = {
            showProfile()
          }) {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Профіль")
          }
        }
      )
    },
    bottomBar = {
      NavigationBar {
        NavigationBarItem(
          icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Список продуктів") },
          label = { Text("Продукти") },
          selected = true, // Поточна сторінка
          onClick = {}
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Кошик") },
          label = { Text("Кошик") },
          selected = false,
          onClick = { LocalStorage.getUser(context)?.let { cartDetails(it.id) } }
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Person, contentDescription = "Мої покупки") },
          label = { Text("Покупки") },
          selected = false,
          onClick = { showProfile() }
        )
      }
    },
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
            val beerProducts = products.filter { it.category.name == "beer" }
            val snackProducts = products.filter { it.category.name == "snack" }

            if (beerProducts.isNotEmpty()) {
              item {
                Text(
                  text = "Пиво",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(bottom = 8.dp)
                )
              }
              item {
                LazyRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  itemsIndexed(beerProducts) { _, product ->
                    ProductCard(product = product, onClick = { showProductDetails(product.id) })
                  }
                }
              }
            }

            if (snackProducts.isNotEmpty()) {
              item {
                Text(
                  text = "Снеки",
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(bottom = 8.dp)
                )
              }
              item {
                LazyRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  itemsIndexed(snackProducts) { _, product ->
                    ProductCard(product = product, onClick = { showProductDetails(product.id) })
                  }
                }
              }
            }
          }
        }
      }
    }
  )
}

@Composable
fun ProductCard(product: ProductDto, onClick: () -> Unit) {
  Column(
    modifier = Modifier
      .width(200.dp)
      .padding(8.dp)
      .clickable(onClick = onClick) // Додаємо клікабельність
      .background(MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium)
      .padding(16.dp)
  ) {
    if (product.images.isNotEmpty()) {
      PicassoImage(
        url = product.images.first().imageUrl,
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(bottom = 8.dp)
      )
    }

    Text(
      text = product.name,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = product.description,
      style = MaterialTheme.typography.bodyMedium,
      color = Color.Gray,
      modifier = Modifier.padding(bottom = 8.dp)
    )
    Text(
      text = "Ціна: ${product.price} грн.",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.primary
    )
  }
}
