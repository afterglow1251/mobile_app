package com.example.myapplication.ui.components.products.models

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.dto.product.ProductDto
import com.example.myapplication.api.network.NetworkModule
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
  var searchText = mutableStateOf("")
  var showFilters = mutableStateOf(false)
  var savedFilters = mutableStateMapOf<String, String?>()
  var products = mutableStateOf<List<ProductDto>>(emptyList())

  fun fetchFilteredProducts(context: Context) {
    viewModelScope.launch {
      try {
        val productService = NetworkModule.getProductService(context)
        products.value = productService.getAllProducts(
          name = searchText.value,
          category = savedFilters["category"],
          manufacturerCountry = savedFilters["manufacturerCountry"],
          manufacturerName = savedFilters["manufacturerName"],
          beerType = savedFilters["beerType"],
          unitSize = savedFilters["unitSize"],
          minPrice = savedFilters["minPrice"]?.toDoubleOrNull(),
          maxPrice = savedFilters["maxPrice"]?.toDoubleOrNull()
        )
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }
}
