package com.example.myapplication.ui.components.products

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.ui.PicassoImage
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.network.NetworkModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(productId: Int, onBack: () -> Unit) {
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  var product by remember { mutableStateOf<ProductDto?>(null) }
  var isLoading by remember { mutableStateOf(false) }

  LaunchedEffect(productId) {
    isLoading = true
    try {
      val productService = NetworkModule.getProductService(context)
      product = productService.getProductById(productId)
    } catch (e: Exception) {
      // Handle error (e.g., show a snackbar)
    } finally {
      isLoading = false
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Деталі продукту") },
        navigationIcon = {
          IconButton(onClick = { onBack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
          }
        }
      )
    }
  ) { innerPadding ->
    if (isLoading) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator()
      }
    } else {
      product?.let {
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
          verticalArrangement = Arrangement.Top,
          horizontalAlignment = Alignment.Start
        ) {
          if (it.images.isNotEmpty()) {
            val pagerState = rememberPagerState(
              initialPageOffsetFraction = 0f,
              pageCount = { it.images.size }
            )
            Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
              HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
              ) { page ->
                PicassoImage(
                  url = it.images[page].imageUrl,
                  modifier = Modifier.fillMaxSize()
                )
              }
            }

            if (it.images.size > 1) {
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                Modifier
                  .fillMaxWidth()
                  .wrapContentHeight()
                  .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
              ) {
                repeat(it.images.size) { iteration ->
                  val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                  Box(
                    modifier = Modifier
                      .padding(4.dp)
                      .clip(if (pagerState.currentPage == iteration) RoundedCornerShape(25.dp) else CircleShape)
                      .background(color)
                      .width(if (pagerState.currentPage == iteration) 16.dp else 10.dp)
                      .height(10.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
            } else {
              Spacer(modifier = Modifier.height(24.dp))
            }

            Text(
              text = it.name,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 8.dp),
              color = MaterialTheme.colorScheme.primary
            )

            Text(
              text = "${it.price} грн",
              fontSize = 24.sp,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
              text = it.description,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
              onClick = { /* Add to cart logic */ },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text("Додати в кошик")
            }
          }
        }
      }
    }
  }
}
