package com.example.myapplication.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.AuthScreen
import com.example.myapplication.ProductListScreen
import com.example.myapplication.utils.TokenManager
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ProfileScreen

@Composable
fun MainNavigation() {
  val context = LocalContext.current
  val navController = rememberNavController()

  val token = TokenManager.getToken(context)
  val startDestination = if (token != null) {
    NavigationScreens.PRODUCTS_LIST.name
  } else {
    NavigationScreens.AUTH.name
  }

  val showProfile: () -> Unit = {
    navController.navigate(NavigationScreens.PROFILE.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToProductsList: () -> Unit = {
    navController.navigate(NavigationScreens.PRODUCTS_LIST.name) {
      popUpTo(NavigationScreens.AUTH.name) { inclusive = true }
    }
  }

  val onLogout: () -> Unit = {
    navController.navigate(NavigationScreens.AUTH.name) {
      TokenManager.removeToken(context = context)
      TokenManager.removeUser(context = context)
      popUpTo(0) // Очищення всього стека
    }
  }

  NavHost(
    navController = navController,
    startDestination = startDestination
  ) {
    composable(route = NavigationScreens.AUTH.name) {
      AuthScreen(onNavigateToProductsList = navigateToProductsList)
    }

    composable(route = NavigationScreens.PRODUCTS_LIST.name) {
      ProductListScreen(showProfile = showProfile)
    }

    composable(route = NavigationScreens.PROFILE.name) {
      ProfileScreen(onBack = { navController.popBackStack() }, onLogout = onLogout)
    }


  }
}
