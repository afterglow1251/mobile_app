package com.example.myapplication.ui.components.cart

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
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
fun CartScreen(userId: Int, onBack: () -> Unit, showMain: () -> Unit, showProfile: () -> Unit, showOrders:() -> Unit, showProductDetails: (Int) -> Unit) {
  val context = LocalContext.current
  var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
  var totalPrice by remember { mutableDoubleStateOf(0.0) }
  var showOrderDialog by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  val userDto = LocalStorage.getUser(context)

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
      errorMessage = null
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
          if (e.code() == 400) {
            val errorResponse = e.response()?.errorBody()?.string()
            val errorData = try {
              val jsonObject = errorResponse?.let { JSONObject(it) }
              val data = jsonObject?.optJSONObject("data")
              val productName = data?.optString("productName")
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
              showOrderDialog = false
              errorMessage = errorData
            }
          } else {
            withContext(Dispatchers.Main) {
              showOrderDialog = false
              errorMessage = "Сталася помилка, спробуйте пізніше"
            }
          }
        } catch (e: Exception) {
          withContext(Dispatchers.Main) {
            showOrderDialog = false
            errorMessage = "Сталася помилка. Перевірте ваше підключення"
          }
        }
      }
    })
  }
  //var showOrderDialog by remember { mutableStateOf(false) }

  //var errorMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Кошик", color = Color.White) },
        actions = {
          IconButton(onClick = { showProfile() }) {
            Icon(imageVector = Icons.Default.Person, contentDescription = "Профіль", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    bottomBar = {
      NavigationBar(
          containerColor = Color(0xFF583E23), // Встановлюємо колір фону
          contentColor = Color.White // Встановлюємо колір тексту та іконок
        ) {
          NavigationBarItem(
            icon = {
              Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Продукти",
                tint = Color.White // Явно задаємо колір іконки, хоча contentColor має це зробити
              )
            },
            label = { Text("Продукти", color = Color.White) }, // Явно задаємо колір тексту, хоча contentColor має це зробити
            selected = false,
            onClick = { showMain() }
          )
        NavigationBarItem(
          icon = {
            Icon(
              Icons.Default.ShoppingBag,
              contentDescription = "Кошик",
              tint = Color(0xFF583E23) // Явно задаємо колір іконки
            )
          },
          label = { Text("Кошик", color = Color.White) }, // Явно задаємо колір тексту
          selected = true,
          onClick = { },
          colors = NavigationBarItemDefaults
            .colors(
              selectedIconColor = Red,
              indicatorColor = Color(0xFFFFFFFF)
            )
        )
          NavigationBarItem(
            icon = {
              Icon(
                Icons.Default.Person,
                contentDescription = "Замовлення",
                tint = Color.White // Явно задаємо колір іконки
              )
            },
            label = { Text("Замовлення", color = Color.White) }, // Явно задаємо колір тексту
            selected = false,
            onClick = { showOrders() },
          )
      }
    }
  ) { innerPadding -> // Ось тут отримуємо innerPadding
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding) // Застосовуємо padding
    ) {
      if (cartItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .weight(1f), // Щоб контент займав весь доступний простір, окрім кнопок
          contentAlignment = Alignment.Center
        ) {
          Text("Кошик порожній", fontSize = 20.sp)
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .weight(1f) // Щоб контент займав весь доступний простір, окрім кнопок
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
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            //.background(Color.White)

        ) {
          Text(
            text = "Загальна вартість: ${"%.2f".format(totalPrice)} грн",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
          )
          Button(
            onClick = { showOrderDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF583E23),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(4.dp),
          ) {
            Text("Оформити замовлення", fontSize = 18.sp)
          }
        }
      }
    }
  }

  if (errorMessage != null) {
    Snackbar(modifier = Modifier.padding(16.dp), action = {
      TextButton(onClick = { errorMessage = null }) {
        Text("Закрити")
      }
    }) {
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
    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBF1DA)),
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp)
      .clickable(onClick = onShowProductDetails),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (cartItem.imageUrl.isNotEmpty() && LocalConfiguration.current.screenWidthDp > 720) {
        PicassoImage(
          url = cartItem.imageUrl, modifier = Modifier
            .size(120.dp)
            .padding(end = 16.dp)
        )
      }

      Column(
        modifier = Modifier
          .weight(1f)
          .padding(end = 16.dp)
      ) {
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

      Button(
        onClick = onRemove,
        modifier = Modifier
          .align(Alignment.Bottom),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp),
      ) {
        Text("Видалити")
      }
    }
  }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDialog(
  userDto: UserDto?, onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit
) {
  var name by remember { mutableStateOf(userDto?.username ?: "") }
  var phone by remember {
    mutableStateOf(
      "+38" + (userDto?.phoneNumber?.removePrefix("+38") ?: "")
    )
  }
  var address by remember { mutableStateOf(userDto?.address ?: "") }

  var nameError by remember { mutableStateOf<String?>(null) }
  var phoneError by remember { mutableStateOf<String?>(null) }
  var addressError by remember { mutableStateOf<String?>(null) }

  val isFormValid = remember(name, phone, address) {
    nameError = if (name.isEmpty()) "Ім'я не може бути порожнім." else null
    phoneError = if (!isValidPhoneNumber(phone)) "Невірний формат номера телефону." else null
    addressError = if (address.isEmpty()) "Адреса не може бути порожньою." else null

    name.isNotEmpty() && isValidPhoneNumber(phone) && address.isNotEmpty()
  }

  AlertDialog(
    onDismissRequest = { onDismiss() },
    modifier = Modifier
      .padding(10.dp)
      .clip(RoundedCornerShape(8.dp))
      .size(width = 500.dp, height = Dp.Unspecified), // Фіксована ширина
    containerColor = Color(0xFFFBF1DA),
    title = {
      Text("Підтвердження замовлення", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    },
    text = {
      Column {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Ім'я") },
          isError = nameError != null,
          modifier = Modifier.fillMaxWidth(),
          colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = Color.Red,
            cursorColor = Color(0xFF583E23),
            focusedLabelColor = Color(0xFF583E23),
            unfocusedLabelColor = Color.Gray,
            errorLabelColor = Color.Red
          )
        )
        if (nameError != null) {
          Text(
            text = nameError!!,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = phone,
          onValueChange = {
            phone = if (it.startsWith("+38")) it else "+38"
          },
          label = { Text("Номер телефону") },
          isError = phoneError != null,
          modifier = Modifier.fillMaxWidth(),
          keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone
          ),
          colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = Color.Red,
            cursorColor = Color(0xFF583E23),
            focusedLabelColor = Color(0xFF583E23),
            unfocusedLabelColor = Color.Gray,
            errorLabelColor = Color.Red
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

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Адреса") },
          isError = addressError != null,
          modifier = Modifier.fillMaxWidth(),
          colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color.Gray,
            errorBorderColor = Color.Red,
            cursorColor = Color(0xFF583E23),
            focusedLabelColor = Color(0xFF583E23),
            unfocusedLabelColor = Color.Gray,
            errorLabelColor = Color.Red
          )
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
            onConfirm(name, phone, address)
          }
        },
        enabled = isFormValid,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(4.dp),
      ) {
        Text("Підтвердити")
      }
    },
    dismissButton = {
      TextButton(
        onClick = { onDismiss() },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.LightGray
        ),
        shape = RoundedCornerShape(4.dp),
      ) {
        Text("Скасувати", color = Color(0xFF583E23))
      }
    }
  )
}

