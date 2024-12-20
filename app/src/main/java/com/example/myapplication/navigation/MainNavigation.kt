package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.AuthScreen
import com.example.myapplication.ProductListScreen

@Composable
fun MainNavigation() {
  val navController = rememberNavController()

  NavHost(
    navController = navController,
    startDestination = NavigationScreens.AUTH.name
  ) {
    composable(route = NavigationScreens.AUTH.name) {
      AuthScreen(navController = navController)
    }

    composable(route = NavigationScreens.PRODUCTS_LIST.name) {
      ProductListScreen()
    }
  }
}
