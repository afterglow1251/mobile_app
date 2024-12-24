package com.example.myapplication.ui.components.cart

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.order.Order
import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.api.dto.user.UserDto
import com.example.myapplication.api.network.NetworkModule
import com.example.myapplication.ui.components.ui.PicassoImage
import com.example.myapplication.utils.LocalStorage
import com.example.myapplication.validators.isValidPhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(userId: Int, onBack: () -> Unit, showProductDetails: (Int) -> Unit) {
  val context = LocalContext.current
  var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
  var totalPrice by remember { mutableDoubleStateOf(0.0) }
  var showOrderDialog by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }  // Для помилки
  val userDto = LocalStorage.getUser(context)

  // Завантаження кошика
  fun loadCart() {
    cartItems = LocalStorage.getCart(context).filter { it.userId == userId }
    totalPrice = cartItems.sumOf { it.price * it.quantity }
  }

  LaunchedEffect(userId) {
    loadCart()
  }

  if (showOrderDialog) {
    OrderDialog(userDto = userDto, onDismiss = {
      showOrderDialog = false
      errorMessage = null  // Скидаємо помилку при закритті модалки
    }, onConfirm = { name, phone, address ->
      CoroutineScope(Dispatchers.IO).launch {
        val requestBody = Order(
          shippingAddress = address,
          username = name,
          phoneNumber = phone,
          items = LocalStorage.getCartForBackend(context)
        )
        try {
          val productService = NetworkModule.getProductService(context)
          productService.createOrder(requestBody)
          withContext(Dispatchers.Main) {
            LocalStorage.clearCartForUser(context)
            showOrderDialog = false
            loadCart()
            onBack()
          }
        } catch (e: HttpException) {
          // Перевіряємо на статус код 400
          if (e.code() == 400) {
            // Якщо статус 400, обробляємо помилку
            val errorResponse = e.response()?.errorBody()?.string()
            val errorData = try {
              val jsonObject = errorResponse?.let { JSONObject(it) }
              val data = jsonObject?.optJSONObject("data")
              val productName = data?.optString("productName")
              val requestedQuantity = data?.optInt("requestedQuantity")
              val availableQuantity = data?.optInt("availableQuantity")

              if (productName != null && requestedQuantity != null && availableQuantity != null) {
                "Продукт $productName стільки-то ви замовили $requestedQuantity, але у нас доступно $availableQuantity. Спробуйте зменшити кількість або замовити пізніше."
              } else {
                "Сталася помилка, спробуйте ще раз. (1)"
              }
            } catch (ex: Exception) {
              "Невідома помилка."
            }
            withContext(Dispatchers.Main) {
              errorMessage = errorData
            }
          } else {
            // Якщо це інша помилка (не 400)
            withContext(Dispatchers.Main) {
              errorMessage = "Сталася помилка, спробуйте пізніше. (2)"
            }
          }
        } catch (e: Exception) {
          // Загальна помилка, наприклад при відсутності з'єднання
          withContext(Dispatchers.Main) {
            errorMessage = "Сталася помилка. Перевірте ваше підключення. (3)"
          }
        }
      }
    })
  }

  Scaffold(topBar = {
    TopAppBar(title = { Text("Кошик") }, navigationIcon = {
      IconButton(onClick = { onBack() }) {
        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
      }
    })
  }, bottomBar = {
    if (cartItems.isNotEmpty()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Загальна вартість: ${"%.2f".format(totalPrice)} грн",
          fontSize = 18.sp,
          modifier = Modifier.padding(bottom = 8.dp)
        )
        Button(
          onClick = { showOrderDialog = true }, modifier = Modifier.fillMaxWidth()
        ) {
          Text("Оформити замовлення", fontSize = 18.sp)
        }
      }
    }
  }) { innerPadding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding), contentAlignment = Alignment.Center
      ) {
        Text("Кошик порожній", fontSize = 20.sp)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        items(items = cartItems, key = { cartItem -> cartItem.productId }) { cartItem ->
          CartItemRow(context = context, cartItem = cartItem, onRemove = {
            LocalStorage.removeFromCart(context, cartItem.productId)
            loadCart()
          }, onQuantityChange = {
            loadCart()
          }, onShowProductDetails = { showProductDetails(cartItem.productId) })
        }
      }
    }
  }

  // Сповіщення про помилку, якщо вона є
  if (errorMessage != null) {
    Snackbar(modifier = Modifier.padding(16.dp), action = {
      TextButton(onClick = { errorMessage = null }) {
        Text("Закрити")
      }
    }) {
      // Покажемо конкретне повідомлення з деталями про продукт
      Text(text = errorMessage ?: "")
    }
  }
}


@Composable
fun CartItemRow(
  context: Context,
  cartItem: CartItem,
  onRemove: () -> Unit,
  onQuantityChange: () -> Unit,
  onShowProductDetails: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .clickable(onClick = onShowProductDetails),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Картинка продукту
      if (cartItem.imageUrl.isNotEmpty()) {
        PicassoImage(
          url = cartItem.imageUrl, modifier = Modifier
            .size(120.dp) // Задаємо розмір картинки
            .padding(end = 16.dp)
        )
      }

      // Інформація про продукт
      Column(
        modifier = Modifier
          .weight(1f)
          .padding(end = 16.dp)
      ) {
        // Назва та ціна
        Text(
          text = cartItem.name,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
          text = "Ціна: ${cartItem.price} грн",
          fontSize = 16.sp,
          modifier = Modifier.padding(bottom = 16.dp)
        )

        // Кнопки для зміни кількості
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          IconButton(
            onClick = {
              if (cartItem.quantity > 1) {
                LocalStorage.updateCartItemQuantity(
                  context, cartItem.productId, cartItem.quantity - 1
                )
                onQuantityChange()
              }
            }, enabled = cartItem.quantity > 1
          ) {
            Icon(Icons.Filled.Remove, contentDescription = "Зменшити кількість")
          }
          Text(
            text = "${cartItem.quantity}", fontSize = 16.sp
          )
          IconButton(onClick = {
            LocalStorage.updateCartItemQuantity(
              context, cartItem.productId, cartItem.quantity + 1
            )
            onQuantityChange()
          }) {
            Icon(Icons.Filled.Add, contentDescription = "Збільшити кількість")
          }
        }
      }

      // Кнопка видалення
      Button(
        onClick = onRemove, modifier = Modifier.align(Alignment.Bottom)
      ) {
        Text("Видалити")
      }
    }
  }
}

@Composable
fun OrderDialog(
  userDto: UserDto?,
  onDismiss: () -> Unit,
  onConfirm: (String, String, String) -> Unit
) {
  var name by remember { mutableStateOf(userDto?.username ?: "") }
  var phone by remember { mutableStateOf("+38" + (userDto?.phoneNumber?.removePrefix("+38") ?: "")) }
  var address by remember { mutableStateOf(userDto?.address ?: "") }

  // Для відображення помилок
  var nameError by remember { mutableStateOf<String?>(null) }
  var phoneError by remember { mutableStateOf<String?>(null) }
  var addressError by remember { mutableStateOf<String?>(null) }

  val isFormValid = remember(name, phone, address) {
    nameError = if (name.isEmpty()) "Ім'я не може бути порожнім." else null
    phoneError = if (!isValidPhoneNumber(phone)) "Невірний формат номера телефону." else null
    addressError = if (address.isEmpty()) "Адреса не може бути порожньою." else null

    // Всі поля мають бути валідними
    name.isNotEmpty() && isValidPhoneNumber(phone) && address.isNotEmpty()
  }

  AlertDialog(
    onDismissRequest = { onDismiss() },
    title = { Text("Підтвердження замовлення") },
    text = {
      Column {
        // Ім'я
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Ім'я") },
          isError = nameError != null,
          modifier = Modifier.fillMaxWidth()
        )
        if (nameError != null) {
          Text(
            text = nameError!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Номер телефону
        OutlinedTextField(
          value = phone,
          onValueChange = {
            // Забезпечуємо, щоб `+38` залишалося на початку
            phone = if (it.startsWith("+38")) it else "+38"
          },
          label = { Text("Номер телефону") },
          isError = phoneError != null,
          modifier = Modifier.fillMaxWidth(),
          keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone
          )
        )
        if (phoneError != null) {
          Text(
            text = phoneError!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Адреса
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Адреса") },
          isError = addressError != null,
          modifier = Modifier.fillMaxWidth()
        )
        if (addressError != null) {
          Text(
            text = addressError!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (isFormValid) {
            // Передаємо номер без префікса "+38"
            onConfirm(name, phone, address)
          }
        },
        enabled = isFormValid
      ) {
        Text("Підтвердити")
      }
    },
    dismissButton = {
      TextButton(onClick = { onDismiss() }) {
        Text("Скасувати")
      }
    }
  )
}
