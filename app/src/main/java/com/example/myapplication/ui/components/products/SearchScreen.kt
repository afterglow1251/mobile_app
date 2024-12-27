package com.example.myapplication.ui.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.api.dto.product.CartItem
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.ui.components.products.models.SearchViewModel
import com.example.myapplication.ui.components.ui.PicassoImage
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
  viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
  onBack: () -> Unit,
  showProductDetails: (Int) -> Unit
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }
  val focusRequester = remember { FocusRequester() }

  LaunchedEffect(Unit) {
    if (viewModel.products.value.isEmpty()) {
      viewModel.fetchFilteredProducts(context)
    }
  }


  Scaffold(containerColor = Color(0xFFFDF8ED),
    topBar = {
      TopAppBar(
        title = { Text("Пошук продуктів", color = Color.White) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(
              imageVector = Icons.Filled.ArrowBack,
              contentDescription = "Назад",
              tint = Color.White
            )
          }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
      )
    },
    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(16.dp)
    ) {
      CompositionLocalProvider(
        LocalTextSelectionColors provides TextSelectionColors(
          handleColor = Color(0xFF583E23),
          backgroundColor = Color(0xFFFFEBCD)
        )
      ) {
        OutlinedTextField(
          value = viewModel.searchText.value,
          onValueChange = { viewModel.searchText.value = it },
          placeholder = { Text("Введіть назву продукту...") },
          keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
          keyboardActions = KeyboardActions(
            onSearch = {
              viewModel.fetchFilteredProducts(context)
              focusRequester.freeFocus()
            }
          ),
          modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
          colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF583E23),
            unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
            errorBorderColor = Color.Red,            // Колір бордюру при помилці
            cursorColor = Color(0xFF583E23),         // Колір курсора
            focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
            unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
            errorLabelColor = Color.Red,          // Колір підказки (placeholder)
          ),
          trailingIcon = {
            IconButton(onClick = { viewModel.showFilters.value = true }) {
              Icon(
                imageVector = Icons.Filled.FilterList,
                contentDescription = "Фільтри",
                tint = Color(0xFF583E23)
              )
            }
          }
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (viewModel.products.value.isEmpty()) {
        Text(
          text = "Товарів не знайдено. Будь ласка, спробуйте пошукати інакше.",
          style = MaterialTheme.typography.bodyMedium,
          color = Color(0xFF583E23),
          modifier = Modifier.padding(16.dp)
        )
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          itemsIndexed(
            items = viewModel.products.value,
            key = { _, product -> product.id }
          ) { _, product ->
            VerticalProductCard(
              product = product,
              onClick = { showProductDetails(product.id) },
              snackbarHostState = snackbarHostState
            )
          }
        }
      }
    }
  }

  if (viewModel.showFilters.value) {
    FilterDialog(
      savedFilters = viewModel.savedFilters,
      onDismiss = { viewModel.showFilters.value = false },
      onApplyFilters = { filters ->
        viewModel.showFilters.value = false
        filters.forEach { (key, value) ->
          viewModel.savedFilters[key] = if (value.isNullOrBlank()) null else value
        }
        viewModel.fetchFilteredProducts(context)
      }
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
  savedFilters: MutableMap<String, String?>,
  onDismiss: () -> Unit,
  onApplyFilters: (Map<String, String?>) -> Unit
) {
  var minPrice by remember { mutableStateOf(savedFilters["minPrice"] ?: "") }
  var maxPrice by remember { mutableStateOf(savedFilters["maxPrice"] ?: "") }

  var categoryBeer by remember { mutableStateOf(savedFilters["category"]?.contains("beer") == true) }
  var categorySnack by remember { mutableStateOf(savedFilters["category"]?.contains("snack") == true) }

  val countries = listOf("Україна", "Німеччина", "Чехія", "Польща")
  val manufacturers = listOf("Львівське", "Kozel", "Zibert", "Corona")
  val beerTypes = listOf("Світле" to "light", "Темне" to "dark")
  val unitSizes = listOf("0.5L", "1L", "1.5L", "2L", "3L", "50G", "100G", "150G", "200G")

  val selectedCountries = remember { mutableStateMapOf<String, Boolean>() }
  val selectedManufacturers = remember { mutableStateMapOf<String, Boolean>() }
  val selectedBeerTypes = remember { mutableStateMapOf<String, Boolean>() }
  val selectedUnitSizes = remember { mutableStateMapOf<String, Boolean>() }

  countries.forEach { country ->
    selectedCountries[country] = savedFilters["manufacturerCountry"]?.contains(country) == true
  }

  manufacturers.forEach { manufacturer ->
    selectedManufacturers[manufacturer] =
      savedFilters["manufacturerName"]?.contains(manufacturer) == true
  }

  beerTypes.forEach { (_, type) ->
    selectedBeerTypes[type] = savedFilters["beerType"]?.contains(type) == true
  }

  unitSizes.forEach { size ->
    selectedUnitSizes[size] = savedFilters["unitSize"]?.contains(size) == true
  }

  val sectionStates = remember {
    mutableStateMapOf(
      "price" to false,
      "categories" to false,
      "countries" to false,
      "manufacturers" to false,
      "beerTypes" to false,
      "unitSizes" to false
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Фільтри")
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close, // Іконка хрестика
            contentDescription = "Закрити",
            tint = Color(0xFF583E23)
          )
        }
      }
    },
    text = {
      LazyColumn {
        // Section 1: Price
        item {
          SectionHeader(
            title = "Ціна",
            isOpen = sectionStates["price"] == true,
            onToggle = { sectionStates["price"] = !(sectionStates["price"] == true) }
          )
          if (sectionStates["price"] == true) {
            Row {
              CompositionLocalProvider(
                LocalTextSelectionColors provides TextSelectionColors(
                  handleColor = Color(0xFF583E23),
                  backgroundColor = Color(0xFFFFEBCD)
                )
              ) {
                OutlinedTextField(
                  value = minPrice,
                  onValueChange = { minPrice = it },
                  label = { Text("Ціна від") },
                  keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f),
                  colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF583E23),
                    unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
                    errorBorderColor = Color.Red,            // Колір бордюру при помилці
                    cursorColor = Color(0xFF583E23),         // Колір курсора
                    focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
                    unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
                    errorLabelColor = Color.Red,          // Колір підказки (placeholder)
                  ),
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              CompositionLocalProvider(
                LocalTextSelectionColors provides TextSelectionColors(
                  handleColor = Color(0xFF583E23),
                  backgroundColor = Color(0xFFFFEBCD)
                )
              ) {
                OutlinedTextField(
                  value = maxPrice,
                  onValueChange = { maxPrice = it },
                  label = { Text("Ціна до") },
                  keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f),
                  colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF583E23),
                    unfocusedBorderColor = Color.Gray,       // Колір бордюру без фокусу
                    errorBorderColor = Color.Red,            // Колір бордюру при помилці
                    cursorColor = Color(0xFF583E23),         // Колір курсора
                    focusedLabelColor = Color(0xFF583E23),   // Колір мітки у фокусі
                    unfocusedLabelColor = Color.Gray,        // Колір мітки без фокусу
                    errorLabelColor = Color.Red,          // Колір підказки (placeholder)
                  ),
                )
              }
            }
          }
        }

        // Section 2: Categories
        item {
          SectionHeader(
            title = "Категорії",
            isOpen = sectionStates["categories"] == true,
            onToggle = { sectionStates["categories"] = !(sectionStates["categories"] == true) }
          )
          if (sectionStates["categories"] == true) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Checkbox(
                checked = categoryBeer,
                onCheckedChange = { categoryBeer = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
              )
              Text("Пиво")
              Spacer(modifier = Modifier.width(16.dp))
              Checkbox(
                checked = categorySnack,
                onCheckedChange = { categorySnack = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
              )
              Text("Снек")
            }
          }
        }

// Section 3: Countries
        item {
          SectionHeader(
            title = "Країна виробник",
            isOpen = sectionStates["countries"] == true,
            onToggle = { sectionStates["countries"] = !(sectionStates["countries"] == true) }
          )
          if (sectionStates["countries"] == true) {
            countries.forEach { country ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = selectedCountries[country] == true,
                  onCheckedChange = { selectedCountries[country] = it },
                  colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
                )
                Text(country)
              }
            }
          }
        }

// Section 4: Manufacturers
        item {
          SectionHeader(
            title = "Виробник",
            isOpen = sectionStates["manufacturers"] == true,
            onToggle = {
              sectionStates["manufacturers"] = !(sectionStates["manufacturers"] == true)
            }
          )
          if (sectionStates["manufacturers"] == true) {
            manufacturers.forEach { manufacturer ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = selectedManufacturers[manufacturer] == true,
                  onCheckedChange = { selectedManufacturers[manufacturer] = it },
                  colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
                )
                Text(manufacturer)
              }
            }
          }
        }

// Section 5: Beer Types
        item {
          SectionHeader(
            title = "Тип пива",
            isOpen = sectionStates["beerTypes"] == true,
            onToggle = { sectionStates["beerTypes"] = !(sectionStates["beerTypes"] == true) }
          )
          if (sectionStates["beerTypes"] == true) {
            beerTypes.forEach { (label, type) ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = selectedBeerTypes[type] == true,
                  onCheckedChange = { selectedBeerTypes[type] = it },
                  colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
                )
                Text(label)
              }
            }
          }
        }

// Section 6: Unit Sizes
        item {
          SectionHeader(
            title = "Розмір одиниці",
            isOpen = sectionStates["unitSizes"] == true,
            onToggle = { sectionStates["unitSizes"] = !(sectionStates["unitSizes"] == true) }
          )
          if (sectionStates["unitSizes"] == true) {
            unitSizes.forEach { size ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                  checked = selectedUnitSizes[size] == true,
                  onCheckedChange = { selectedUnitSizes[size] = it },
                  colors = CheckboxDefaults.colors(checkedColor = Color(0xFF583E23))
                )
                Text(size)
              }
            }
          }
        }

      }
    },
    confirmButton = {
      Button(
        onClick = {
          val appliedFilters = mutableMapOf<String, String?>()
          if (minPrice.isNotBlank()) appliedFilters["minPrice"] = minPrice
          if (maxPrice.isNotBlank()) appliedFilters["maxPrice"] = maxPrice

          appliedFilters["category"] = listOfNotNull(
            if (categoryBeer) "beer" else null,
            if (categorySnack) "snack" else null
          ).joinToString(",")

          appliedFilters["manufacturerCountry"] =
            selectedCountries.filter { it.value }.keys.joinToString(",")
          appliedFilters["manufacturerName"] =
            selectedManufacturers.filter { it.value }.keys.joinToString(",")
          appliedFilters["beerType"] = selectedBeerTypes.filter { it.value }.keys.joinToString(",")
          appliedFilters["unitSize"] = selectedUnitSizes.filter { it.value }.keys.joinToString(",")

          onApplyFilters(appliedFilters)
        },
        modifier = Modifier
          .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF583E23), // Колір фону кнопки
          contentColor = Color.White         // Колір тексту кнопки
        ),
        shape = RoundedCornerShape(4.dp)
      ) {
        Text("Застосувати")
      }
    }
  )
}

@Composable
fun SectionHeader(title: String, isOpen: Boolean, onToggle: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onToggle() }
      .padding(8.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(text = title, style = MaterialTheme.typography.titleMedium)
    Icon(
      imageVector = if (isOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
      contentDescription = if (isOpen) "Закрити" else "Розкрити"
    )
  }
}

@Composable
fun VerticalProductCard(
  product: ProductDto,
  onClick: () -> Unit,
  snackbarHostState: SnackbarHostState
) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val isOutOfStock = product.quantity <= 0

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(8.dp)
      .background(
        color = if (isOutOfStock) Color.Gray.copy(alpha = 0.2f) else Color(0xFFFBF1DA),
        shape = MaterialTheme.shapes.medium
      )
      .padding(16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (product.images.isNotEmpty() && product.images.firstOrNull()?.imageUrl?.isNotBlank() == true) {
      PicassoImage(
        url = product.images.first().imageUrl,
        modifier = Modifier
          .size(100.dp)
          .padding(end = 16.dp)
          .alpha(if (isOutOfStock) 0.5f else 1f)
      )
    }
    Column(
      modifier = Modifier.weight(1f)
    ) {
      Text(
        text = product.name,
        style = MaterialTheme.typography.bodyLarge,
        color = if (isOutOfStock) Color.Gray else Color(0xFF583E23),
        fontWeight = FontWeight.Bold
      )
      Text(
        text = product.description,
        style = MaterialTheme.typography.bodyMedium,
        color = if (isOutOfStock) Color.Gray else Color.Black,
        modifier = Modifier
          .padding(vertical = 4.dp)
          .widthIn(max = 185.dp),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
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
        color = if (isOutOfStock) Color.Gray else Color(0xFFFBF1DA)
      )
    }
    if (!isOutOfStock) {
      Button(
        onClick = {
          val user = LocalStorage.getUser(context)
          if (user != null) {
            val currentCart = LocalStorage.getCart(context)
              .filter { it.userId == user.id }

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
              snackbarHostState.showSnackbar("Увійдіть, щоб додати товар у кошик!")
            }
          }
        },
        modifier = Modifier
          .width(48.dp), // Ширина кнопки
        // .height(48.dp) // Висота кнопки збільшена до 90dp
        //.padding(start = 8.dp), // Додатковий відступ зліва
        shape = RoundedCornerShape(4.dp), // Закруглення 4dp
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF583E23))
      ) {
        Text(
          text = "+",
          style = MaterialTheme.typography.displayMedium.copy(fontSize = 36.sp), // Збільшено розмір тексту
          color = Color.White,
          // fontWeight = FontWeight.Bold
        )
      }


    }
  }
}
