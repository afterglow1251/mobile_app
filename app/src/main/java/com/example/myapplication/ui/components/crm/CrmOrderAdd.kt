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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.api.dto.wholesale.order.CreateWholesaleOrderDto
import com.example.myapplication.api.dto.wholesale.order.CreateWholesaleOrderItemDto
import kotlinx.coroutines.launch

// Клас для представлення товару в замовленні
data class OrderProduct(
  val id: Int,
  val name: String,
  val quantity: Int,
  val price: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmOrderAdd(onBack: () -> Unit, customerId: Int) {
  val context = LocalContext.current
  val productService = NetworkModule.getProductService(context)
  val wholesaleOrderService = NetworkModule.getWholesaleOrderService(context)
  var productName by remember { mutableStateOf("") }
  var productQuantity by remember { mutableStateOf("") }
  var productPrice by remember { mutableStateOf("") }
  val products = remember { mutableStateListOf<OrderProduct>() }
  var suggestions by remember { mutableStateOf(listOf<OrderProduct>()) }
  var selectedProduct by remember { mutableStateOf<OrderProduct?>(null) }
  val coroutineScope = rememberCoroutineScope()
  var isAddingProduct by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }

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
        products.forEachIndexed { index, product ->
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), shape = MaterialTheme.shapes.medium)
              .padding(16.dp),
          ) {
            Column {
              Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 22.sp,
                modifier = Modifier.padding(bottom = 8.dp)
              )
              Text(
                text = "Ціна: ${product.price} грн",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp
              )
              Text(
                text = "Кількість: ${product.quantity}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp

              )
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = {
                  products.removeAt(index)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.align(Alignment.End)
              ) {
                Text("Видалити", color = Color.White)
              }
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
          if (errorMessage.isNotEmpty()) {
            Text(
              text = errorMessage,
              color = Color.Red,
              modifier = Modifier.padding(bottom = 8.dp)
            )
          }

          OutlinedTextField(
            value = productName,
            onValueChange = {
              productName = it
              coroutineScope.launch {
                suggestions = if (it.isNotEmpty()) {
                  try {
                    productService.getAllProducts(name = it).map { product ->
                      OrderProduct(
                        id = product.id,
                        name = product.name,
                        quantity = 0,
                        price = product.price // Default price from DB
                      )
                    }
                  } catch (e: Exception) {
                    listOf()
                  }
                } else {
                  listOf()
                }
              }
            },
            label = { Text("Уведіть назву товару") },
            modifier = Modifier.fillMaxWidth()
          )

          if (suggestions.isNotEmpty()) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(8.dp)
            ) {
              suggestions.forEach { suggestion ->
                Text(
                  text = suggestion.name,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .clickable {
                      selectedProduct = suggestion
                      productName = suggestion.name
                      suggestions = listOf()
                    }
                )
              }
            }
          }

          OutlinedTextField(
            value = productQuantity,
            onValueChange = { productQuantity = it },
            label = { Text("Кількість") },
            modifier = Modifier.fillMaxWidth(),
            isError = productQuantity.isEmpty()
          )
          if (productQuantity.isEmpty()) {
            Text(
              text = "Кількість не може бути порожньою",
              color = Color.Red,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }

          OutlinedTextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("Ціна (необов'язково)") },
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(4.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
              onClick = {
                isAddingProduct = false
                errorMessage = ""
              }
            ) {
              Text("Скасувати")
            }
            Button(
              onClick = {
                if (productName.isNotEmpty() && productQuantity.isNotEmpty()) {
                  val quantity = productQuantity.toIntOrNull()
                  val price = productPrice.toDoubleOrNull() ?: selectedProduct?.price
                  if (quantity != null && price != null && selectedProduct != null) {
                    val exists = products.any { it.id == selectedProduct!!.id }
                    if (!exists) {
                      products.add(
                        OrderProduct(
                          id = selectedProduct!!.id,
                          name = selectedProduct!!.name,
                          quantity = quantity,
                          price = price
                        )
                      )
                      productName = ""
                      productQuantity = ""
                      productPrice = ""
                      selectedProduct = null
                      isAddingProduct = false
                    } else {
                      errorMessage = "Цей товар вже додано"
                    }
                  }
                }
              },
              modifier = Modifier.weight(1f)
            ) {
              Text("Додати товар")
            }
          }
        }
      }

      Button(
        onClick = { isAddingProduct = true },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Додати товар")
      }

      Button(
        onClick = {
          coroutineScope.launch {
            try {
              val createOrderDto = CreateWholesaleOrderDto(
                customerId = customerId,
                items = products.map {
                  CreateWholesaleOrderItemDto(
                    productId = it.id,
                    quantity = it.quantity,
                    wholesalePrice = it.price
                  )
                }
              )
              wholesaleOrderService.createOrder(createOrderDto)
              errorMessage = "Замовлення успішно створено"
              onBack()
            } catch (e: Exception) {
              errorMessage = "Помилка створення замовлення"
            }
          }
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
