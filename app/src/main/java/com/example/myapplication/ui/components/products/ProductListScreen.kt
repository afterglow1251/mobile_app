package com.example.myapplication.ui.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.network.NetworkModule
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.ui.components.ui.PicassoImage
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
  showProfile: () -> Unit,
  showProductDetails: (Int) -> Unit,
  cartDetails: (Int) -> Unit,
  showOrders: () -> Unit,
  navigateToSearch: () -> Unit
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val scope = rememberCoroutineScope()

  var products by remember { mutableStateOf<List<ProductDto>>(emptyList()) }
  var isLoading by remember { mutableStateOf(false) }

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



  Scaffold(containerColor = Color(0xFFFDF8ED), topBar = {
    TopAppBar(title = { Text("Список продуктів", color = Color.White) },
      actions = {
      IconButton(onClick = { showProfile() }) {
        Icon(imageVector = Icons.Default.Person, contentDescription = "Профіль", tint = Color.White)
      }
      }, colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
    )
  }, snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, bottomBar = {
    NavigationBar(containerColor = Color(0xFF583E23), // Встановлюємо колір фону
    contentColor = Color.White // Встановлюємо колір тексту та іконок)
    ) {

      NavigationBarItem(
        icon = {
          Icon(
            Icons.Default.ShoppingCart,
            contentDescription = "Продукти",
            tint = Color(0xFF583E23) // Явно задаємо колір іконки, хоча contentColor має це зробити
          )
        },
        label = { Text("Продукти", color = Color.White) }, // Явно задаємо колір тексту, хоча contentColor має це зробити
        selected = true,
        onClick = {},
        colors = androidx.compose.material3.NavigationBarItemDefaults
          .colors(
            selectedIconColor = Red,
            // indicatorColor = Color(0xFFFDF8ED)
            indicatorColor = Color(0xFFFFFFFF)
          )
      )
      NavigationBarItem(
        icon = {
          Icon(
            Icons.Default.ShoppingBag,
            contentDescription = "Кошик",
            tint = Color.White // Явно задаємо колір іконки
          )
        },
        label = { Text("Кошик", color = Color.White) }, // Явно задаємо колір тексту
        selected = false,
        onClick = { LocalStorage.getUser(context)?.let { cartDetails(it.id) } }
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
  }, content = { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      Box(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { navigateToSearch() }
      ) {
        OutlinedTextField(
          value = "",
          onValueChange = { },
          placeholder = { Text("Пошук продуктів...", color = Color(0xFF583E23)) },
          leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Пошук", tint = Color(0xFF583E23))
          },
          readOnly = false,
          enabled = false,
          modifier = Modifier.fillMaxWidth(),
          colors = TextFieldDefaults.outlinedTextFieldColors(
            //focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color(0xFF583E23),       // Колір бордюру без фокусу
            //errorBorderColor = Color.Red,            // Колір бордюру при помилці
            //cursorColor = Color(0xFF583E23),         // Колір курсора
            //focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
            //unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
            //errorLabelColor = Color.Red,          // Колір підказки (placeholder)
          ),
        )
      }

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
                color = Color(0xFF583E23),
                modifier = Modifier.padding(bottom = 8.dp)
              )
            }
            item {
              LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                itemsIndexed(items = beerProducts,
                  key = { _, product -> product.id }) { _, product ->
                  ProductCard(
                    product = product,
                    onClick = { showProductDetails(product.id) },
                    snackbarHostState = snackbarHostState
                  )
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
                color = Color(0xFF583E23),
                modifier = Modifier.padding(bottom = 8.dp)
              )
            }
            item {
              LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                itemsIndexed(items = snackProducts,
                  key = { _, product -> product.id }) { _, product ->
                  ProductCard(
                    product = product,
                    onClick = { showProductDetails(product.id) },
                    snackbarHostState = snackbarHostState
                  )
                }
              }
            }
          }
        }
      }
    }
  })
}


@Composable
fun ProductCard(product: ProductDto, onClick: () -> Unit, snackbarHostState: SnackbarHostState) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val isOutOfStock = product.quantity <= 0

  Column(
    modifier = Modifier
      .width(200.dp)
      .padding(8.dp)
      .clickable(onClick = onClick)
      .background(
        color = if (isOutOfStock) Color.Gray.copy(alpha = 0.2f) else Color(0xFFFBF1DA),
        shape = MaterialTheme.shapes.medium
      )
      .padding(16.dp)
  ) {
    if (product.images.isNotEmpty()) {
      PicassoImage(
        url = product.images.first().imageUrl,
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .padding(bottom = 8.dp)
          .alpha(if (isOutOfStock) 0.5f else 1f)
      )
    }

    Text(
      text = product.name,
      style = MaterialTheme.typography.bodyLarge,
      fontWeight = FontWeight.Bold,
      color = if (isOutOfStock) Color.Gray else MaterialTheme.colorScheme.onSurface,
      modifier = Modifier.padding(bottom = 4.dp)
    )
    Text(
      text = product.description,
      style = MaterialTheme.typography.bodyMedium,
      color = if (isOutOfStock) Color.Gray else Color.Black,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    if (isOutOfStock) {
      Text(
        text = "Немає на складі",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.Red,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
    }

    Text(
      text = "Ціна: ${product.price} грн.",
      style = MaterialTheme.typography.bodyMedium,
      color = if (isOutOfStock) Color.Gray else Color(0xFF583E23)
    )

    if (!isOutOfStock) {
      Spacer(modifier = Modifier.height(8.dp))
      Button(
        onClick = {
          val user = LocalStorage.getUser(context)
          if (user != null) {
            val currentCart = LocalStorage.getCart(context).filter { it.userId == user.id }

            val isInCart = currentCart.any { cartItem -> cartItem.productId == product.id }
            if (isInCart) {
              scope.launch {
                snackbarHostState.showSnackbar(
                  message = "Цей товар вже є в кошику",
                  actionLabel = "ОК",
                  duration = SnackbarDuration.Short
                )
              }
            } else {
              val cartItem = CartItem(
                userId = user.id,
                productId = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                category = product.category,
                imageUrl = product.images.firstOrNull()?.imageUrl.orEmpty(),
                quantity = 1
              )
              LocalStorage.addToCart(context, cartItem)
              scope.launch {
                snackbarHostState.showSnackbar(
                  message = "Товар додано в кошик",
                  actionLabel = "ОК",
                  duration = SnackbarDuration.Short
                )
              }
            }
          } else {
            scope.launch {
              snackbarHostState.showSnackbar("Увійдіть, щоб додати товар у кошик")
            }
          }
        },
        modifier = Modifier
          .fillMaxWidth() // Кнопка займає всю ширину блоку
          .padding(4.dp),
        shape = RoundedCornerShape(4.dp), // Закруглення кнопки 4 пікселі
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF583E23))
      ) {
        Text("+", fontSize = 36.sp, color = Color.White)
      }
    }


  }
}
