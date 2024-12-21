package com.example.myapplication.components.products

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.network.NetworkModule
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.squareup.picasso.Picasso
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(showProfile: () -> Unit) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }


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
      snackbarHostState.showSnackbar("Сталася помилка: ${e.localizedMessage}")
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
    content = { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
      ) {
        // Вміст екрану з продуктами
        if (isLoading) {
          CircularProgressIndicator()
        } else {
          // Горизонтальна прокрутка для продуктів (наприклад, Пиво, Снеки)
          val beerProducts = products.filter { it.category.name == "beer" }
          val snackProducts = products.filter { it.category.name == "snack" }

          if (beerProducts.isNotEmpty()) {
            Text(
              text = "Пиво",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              itemsIndexed(beerProducts) { _, product ->
                ProductCard(product)
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          if (snackProducts.isNotEmpty()) {
            Text(
              text = "Снеки",
              fontSize = 20.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              itemsIndexed(snackProducts) { _, product ->
                ProductCard(product)
              }
            }
          }
        }
      }
    }
  )
}

@Composable
fun PicassoImage(url: String, modifier: Modifier = Modifier) {
  AndroidView(
    modifier = modifier,
    factory = { context ->
      ImageView(context).apply {
        Picasso.get()
          .load(url)
          .into(this) // Завантажуємо зображення за допомогою Picasso
      }
    }
  )
}

@Composable
fun ProductCard(product: ProductDto) {
  Column(
    modifier = Modifier
      .width(200.dp)  // Встановлюємо фіксовану ширину для картки
      .padding(8.dp)
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
