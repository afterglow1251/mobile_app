package com.example.myapplication.ui.components.crm

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

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
  var errorMessage2 by remember { mutableStateOf("") }
  val snackbarHostState = remember { SnackbarHostState() }
  var showDeleteDialog by remember { mutableStateOf<Pair<Boolean, Int>?>(null) }

  if (errorMessage2.isNotEmpty()) {
    LaunchedEffect(errorMessage2) {
      snackbarHostState.showSnackbar(errorMessage2)
      errorMessage2 = ""
    }
  }

  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Додати оптове замовлення", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    snackbarHost = {
      SnackbarHost(hostState = snackbarHostState)
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
              .background(Color(0xFFFBF1DA), shape = MaterialTheme.shapes.medium)
              .padding(16.dp),
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(modifier = Modifier.weight(1f)) {
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
              }
              IconButton(onClick = { showDeleteDialog = Pair(true, index) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Видалити")
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
            .background(Color(0xFFFBF1DA))
            .padding(horizontal = 16.dp, vertical = 8.dp) // Симетричне вирівнювання
        ) {
          if (errorMessage.isNotEmpty()) {
            Text(
              text = errorMessage,
              color = Color.Red,
              modifier = Modifier.padding(bottom = 8.dp)
            )
          }

          CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
              handleColor = Color(0xFF583E23),
              backgroundColor = Color(0xFFFFEBCD)
            )
          ) {
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
                          price = product.price
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
              modifier = Modifier.fillMaxWidth(),
              colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF583E23),
                unfocusedBorderColor = Color.Gray,
                errorBorderColor = Color.Red,
                cursorColor = Color(0xFF583E23),
                focusedLabelColor = Color(0xFF583E23),
                unfocusedLabelColor = Color.Gray,
                errorLabelColor = Color.Red
              ),
            )
          }

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

          CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
              handleColor = Color(0xFF583E23),
              backgroundColor = Color(0xFFFFEBCD)
            )
          ) {OutlinedTextField(
            value = productQuantity,
            onValueChange = { productQuantity = it },
            label = { Text("Кількість") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Color(0xFF583E23),
              unfocusedBorderColor = Color.Gray,
              errorBorderColor = Color.Red,
              cursorColor = Color(0xFF583E23),
              focusedLabelColor = Color(0xFF583E23),
              unfocusedLabelColor = Color.Gray,
              errorLabelColor = Color.Red
            ),
            isError = productQuantity.isEmpty()
          )}
          if (productQuantity.isEmpty()) {
            Text(
              text = "Кількість не може бути порожньою",
              color = Color.Red,
              fontSize = 12.sp,
              modifier = Modifier.padding(top = 4.dp)
            )
          }

          CompositionLocalProvider(
            LocalTextSelectionColors provides TextSelectionColors(
              handleColor = Color(0xFF583E23),
              backgroundColor = Color(0xFFFFEBCD)
            )
          ) {OutlinedTextField(
            value = productPrice,
            onValueChange = { productPrice = it },
            label = { Text("Ціна (необов'язково)") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
              focusedBorderColor = Color(0xFF583E23),
              unfocusedBorderColor = Color.Gray,
              errorBorderColor = Color.Red,
              cursorColor = Color(0xFF583E23),
              focusedLabelColor = Color(0xFF583E23),
              unfocusedLabelColor = Color.Gray,
              errorLabelColor = Color.Red
            ),
          )}

          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Button(
              onClick = {
                isAddingProduct = false
                errorMessage = ""
              },
              modifier = Modifier.padding(end = 8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF583E23),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(4.dp),
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
              modifier = Modifier.weight(1f),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF583E23),
                contentColor = Color.White
              ),
              shape = RoundedCornerShape(4.dp),
            ) {
              Text("Додати товар")
            }
          }
        }
      }


      Spacer(modifier = Modifier.height(8.dp))

      Button(
        onClick = { isAddingProduct = true },
        modifier = Modifier.fillMaxWidth(),colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp),
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
              errorMessage2 = "Замовлення успішно створено"
              snackbarHostState.showSnackbar(errorMessage2)
              onBack()
            } catch (e: HttpException) {
              if (e.code() == 400) {
                val errorResponse = e.response()?.errorBody()?.string()
                val errorData = try {
                  val jsonObject = errorResponse?.let { JSONObject(it) }
                  val data = jsonObject?.optJSONObject("data")
                  val productName = jsonObject?.optString("productName")
                  val requestedQuantity = data?.optInt("requestedQuantity")
                  val availableQuantity = data?.optInt("availableQuantity")

                  if (productName != null && requestedQuantity != null && availableQuantity != null) {
                    "Продукт $productName замовлено $requestedQuantity шт., але доступно $availableQuantity шт. Спробуйте зменшити кількість або замовити пізніше."
                  } else {
                    "Сталася помилка, спробуйте ще раз"
                  }
                } catch (ex: Exception) {
                  "Невідома помилка"
                }
                withContext(Dispatchers.Main) {
                  errorMessage2 = errorData
                  snackbarHostState.showSnackbar(errorMessage2)
                }
              } else {
                withContext(Dispatchers.Main) {
                  errorMessage2 = "Сталася помилка, спробуйте пізніше"
                  snackbarHostState.showSnackbar(errorMessage2)
                }
              }
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp),
        enabled = products.isNotEmpty()
      ) {
        Text("Створити замовлення")
      }

      if (showDeleteDialog != null) {
        AlertDialog(
          onDismissRequest = { showDeleteDialog = null },
          text = { Text("Ви впевнені, що хочете видалити цей товар?") },
          confirmButton = {
            TextButton(onClick = {
              products.removeAt(showDeleteDialog!!.second)
              showDeleteDialog = null
            }) {
              Text("Видалити")
            }
          },
          dismissButton = {
            TextButton(onClick = { showDeleteDialog = null }) {
              Text("Скасувати")
            }
          }
        )
      }
    }
  }
}