package com.example.myapplication

import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun ProductListScreen() {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // Стан для продуктів
  var products by remember { mutableStateOf<List<ProductDto>>(emptyList()) }
  var isLoading by remember { mutableStateOf(false) }

  // Завантажуємо продукти з API
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

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Top,
    horizontalAlignment = Alignment.Start
  ) {
    Text(
      text = "СПИСОК ПРОДУКТІВ",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Показуємо індикатор завантаження, поки продукти завантажуються
    if (isLoading) {
      CircularProgressIndicator()
    }

    // Виводимо продукти
    LazyColumn(
      modifier = Modifier.fillMaxSize()
    ) {
      itemsIndexed(products) { _, product ->
        ProductCard(product)
      }
    }
  }
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
      .fillMaxWidth()
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
    Text(
      text = product.images.first().imageUrl,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(bottom = 4.dp)
    )
  }
}
