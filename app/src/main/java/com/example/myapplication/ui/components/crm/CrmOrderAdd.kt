package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Клас для представлення товару
data class Product(
  val name: String,
  val quantity: String,
  val price: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderAdd(onBack: () -> Unit) {
  var productName by remember { mutableStateOf("") }
  var productQuantity by remember { mutableStateOf("") }
  var productPrice by remember { mutableStateOf("") }
  val products = remember { mutableStateListOf<Product>() }
  val suggestions = listOf("Товар 1", "Товар 2", "Товар 3", "Товар 4")

  var isAddingProduct by remember { mutableStateOf(false) }
  var isProductNameConfirmed by remember { mutableStateOf(false) }
  var showSuggestions by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Додати оптове замовлення") },
        navigationIcon = {
          IconButton(onClick = onBack) {
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
        .verticalScroll(rememberScrollState()),
      verticalArrangement = Arrangement.Top
    ) {
      Text(
        text = "Додані у замовлення товари:",
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(bottom = 16.dp)
      )
      if (products.isEmpty()) {
        Text(
          text = "Поки товарів не додано",
          style = MaterialTheme.typography.bodyLarge,
          color = Color.Gray,
          modifier = Modifier.padding(bottom = 16.dp)
        )
      } else {
        products.forEach { product ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = MaterialTheme.shapes.medium)
              .padding(16.dp)
          ) {
            Column {
              Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 8.dp)
              )
              Text(
                text = "Кількість: ${product.quantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 4.dp)
              )
              Text(
                text = "Вартість: ${product.price} грн.",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
              )
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }
      }

      if (isAddingProduct) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
            .padding(16.dp)
        ) {

          Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
              value = productName,
              onValueChange = {
                if (!isProductNameConfirmed) {
                  productName = it
                  showSuggestions = it.isNotEmpty()
                }
              },
              label = { Text("Уведіть назву товару") },
              enabled = !isProductNameConfirmed,
              modifier = Modifier.width(200.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
              onClick = {
                isProductNameConfirmed = productName.isNotEmpty()
                showSuggestions = false
              },
              modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight)
            ) {
              Text("Підтвердити")
            }
          }

          if (showSuggestions) {
            Column(
              modifier = Modifier
                .width(200.dp)
                .background(Color.LightGray)
                .padding(end = 8.dp)
            ) {
              suggestions.filter { it.contains(productName, ignoreCase = true) }.forEach { suggestion ->
                Text(
                  text = suggestion,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                      productName = suggestion
                      showSuggestions = false
                    }
                )
              }
            }
          }

          if (isProductNameConfirmed) {
            OutlinedTextField(
              value = productQuantity,
              onValueChange = { productQuantity = it },
              label = { Text("Кількість") },
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = productPrice,
              onValueChange = { productPrice = it },
              label = { Text("Вартість") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Button(
              onClick = {
                if (productName.isNotEmpty() && productQuantity.isNotEmpty() && productPrice.isNotEmpty()) {
                  products.add(Product(productName, productQuantity, productPrice))
                  productQuantity = ""
                  productPrice = ""
                  isAddingProduct = false
                  isProductNameConfirmed = false
                }
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Додати товар")
            }
          }
        }
        Spacer(modifier = Modifier.height(8.dp))
      }

      Button(
        onClick = { isAddingProduct = true },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Додати товар")
      }

      Button(
        onClick = {
          println("Замовлення створено: $products")
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        enabled = products.isNotEmpty()
      ) {
        Text("Створити замовлення")
      }
    }
  }
}
