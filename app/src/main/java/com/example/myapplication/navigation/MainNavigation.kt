package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.products.ProductListScreen
import com.example.myapplication.utils.LocalStorage
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.components.auth.AuthScreen
import com.example.myapplication.ui.components.products.ProductDetailsScreen
import com.example.myapplication.ui.components.profile.EditProfileScreen
import com.example.myapplication.ui.components.profile.ProfileScreen

@Composable
fun MainNavigation() {
  val context = LocalContext.current
  val navController = rememberNavController()

  val token = LocalStorage.getToken(context)
  val startDestination = if (token != null) {
    NavigationScreens.PRODUCTS_LIST.name
  } else {
    NavigationScreens.AUTH.name
  }

  val EditProfile: () -> Unit = {
    navController.navigate(NavigationScreens.PROFILE_EDIT.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showProfile: () -> Unit = {
    navController.navigate(NavigationScreens.PROFILE.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showProductDetails: (Int) -> Unit = { productId ->
    navController.navigate("${NavigationScreens.PRODUCT_DETAIL.name}/$productId") {
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
      LocalStorage.removeToken(context)
      LocalStorage.removeUser(context)
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
      ProductListScreen(showProfile = showProfile, showProductDetails = showProductDetails)
    }

    composable(route = NavigationScreens.PROFILE.name) {
      ProfileScreen(onBack = { navController.popBackStack() }, onLogout = onLogout, editProfile = EditProfile)
    }

    composable(route = NavigationScreens.PROFILE_EDIT.name) {
      EditProfileScreen(onBack = { navController.popBackStack() })
    }

    composable(route = "${NavigationScreens.PRODUCT_DETAIL.name}/{productId}") { backStackEntry ->
      val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
      if (productId != null) {
        ProductDetailsScreen(productId = productId, onBack = { navController.popBackStack() })
      }
    }
  }
}
