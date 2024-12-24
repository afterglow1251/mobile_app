package com.example.myapplication.navigation

import ClientListScreen
import CrmOrderDetailsScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.products.ProductListScreen
import com.example.myapplication.utils.LocalStorage
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.components.auth.AuthScreen

import com.example.myapplication.ui.components.cart.CartScreen
import com.example.myapplication.ui.components.crm.CrmArchiveMonthScreen
import com.example.myapplication.ui.components.crm.CrmArchiveScreen
import com.example.myapplication.ui.components.crm.CrmArchiveYearScreen
import com.example.myapplication.ui.components.crm.CrmClientAdd
import com.example.myapplication.ui.components.crm.CrmClientDetails

// import com.example.myapplication.ui.components.cart.CartScreen
import com.example.myapplication.ui.components.crm.CrmMainScreen
import com.example.myapplication.ui.components.crm.CrmOrderAdd

import com.example.myapplication.ui.components.crm.CrmOrderListScreen
import com.example.myapplication.ui.components.crm.CrmStatsScreen
import com.example.myapplication.ui.components.order.OrderDetailsScreen

import com.example.myapplication.ui.components.order.OrderListScreen
import com.example.myapplication.ui.components.products.ProductDetailsScreen
import com.example.myapplication.ui.components.products.SearchScreen
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

  val editProfile: () -> Unit = {
    navController.navigate(NavigationScreens.PROFILE_EDIT.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showOrders: () -> Unit = {
    navController.navigate(NavigationScreens.ORDERS.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showProfile: () -> Unit = {
    navController.navigate(NavigationScreens.PROFILE.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToSearch: () -> Unit = {
    navController.navigate(NavigationScreens.SEARCH_SCREEN.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showProductDetails: (Int) -> Unit = { productId ->
    navController.navigate("${NavigationScreens.PRODUCT_DETAIL.name}/$productId") {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val showOrderDetails: (Int) -> Unit = { orderId ->
    navController.navigate("${NavigationScreens.ORDER_DETAILS.name}/$orderId") {
      launchSingleTop = true
    }
  }

  val cartDetails: (Int) -> Unit = { userId ->
    navController.navigate("${NavigationScreens.CART.name}/$userId") {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }


  val navigateToProductsList: () -> Unit = {
    navController.navigate(NavigationScreens.PRODUCTS_LIST.name) {
      popUpTo(NavigationScreens.AUTH.name) { inclusive = true }
    }
  }

  val onLogout: () -> Unit = {
    LocalStorage.logoutUser(context) // Очищення токена, користувача та кошика
    navController.navigate(NavigationScreens.AUTH.name) {
      popUpTo(0) // Очищення всього стека навігації
    }
  }


  val navigateToCrmMain: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_MAIN.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmStats: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_STATS.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmArchive: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_ARCHIVE.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmMonthArchive: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_MONTH.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmYearArchive: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_YEAR.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmClientList: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_CLIENT_LIST.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateToCrmClientDetails: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_CLIENT_DETAILS.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateCrmOrderList: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_ORDER_LIST.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateCrmOrderDetails: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_ORDER_DETAIL.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateCrmClientAdd: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_CLIENT_ADD.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
    }
  }

  val navigateCrmOrderAdd: () -> Unit = {
    navController.navigate(NavigationScreens.CRM_ORDER_ADD.name) {
      launchSingleTop = true // Запобігаємо дублюванню екрану
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
      ProductListScreen(
        showProfile = showProfile,
        showProductDetails = showProductDetails,
        cartDetails = cartDetails,
        showOrders = showOrders,
        navigateToSearch = navigateToSearch
      )
    }

    composable(route = NavigationScreens.PROFILE.name) {
      ProfileScreen(onBack = { navController.popBackStack() }, onLogout = onLogout, editProfile = editProfile, navigateToCrmMain = navigateToCrmMain)
    }

    composable(route = NavigationScreens.ORDERS.name) {
      OrderListScreen(cartDetails = cartDetails, showMain = navigateToProductsList, onShowOrderDetails = showOrderDetails)
    }

    composable(route = NavigationScreens.PROFILE_EDIT.name) {
      EditProfileScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.SEARCH_SCREEN.name) {
      SearchScreen(
        onBack = { navController.popBackStack() },
        showProductDetails = showProductDetails,)
    }

    composable(route = "${NavigationScreens.PRODUCT_DETAIL.name}/{productId}") { backStackEntry ->
      val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
      if (productId != null) {
        ProductDetailsScreen(productId = productId, onBack = { navController.popBackStack() })
      }
    }

    composable(route = "${NavigationScreens.CART.name}/{userId}") { backStackEntry ->
      val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
      if (userId != null) {
        CartScreen(userId = userId, onBack = { navController.popBackStack() }, showProductDetails = showProductDetails)
      }
    }

    composable(route = "${NavigationScreens.ORDER_DETAILS.name}/{orderId}") { backStackEntry ->
      val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
      if (orderId != null) {
        OrderDetailsScreen(orderId = orderId, onBack = { navController.popBackStack() })
      }
    }


    composable(route = NavigationScreens.CRM_MAIN.name) {
      CrmMainScreen(onBack = { navController.popBackStack() }, navigateToCrmStats = navigateToCrmStats, navigateToCrmClientList = navigateToCrmClientList)
    }

    composable(route = NavigationScreens.CRM_STATS.name) {
      CrmStatsScreen(onBack = { navController.popBackStack() }, navigateToCrmArchive = navigateToCrmArchive)
    }

    composable(route = NavigationScreens.CRM_ARCHIVE.name) {
      CrmArchiveScreen(onBack = { navController.popBackStack() }, navigateToCrmMonthArchive = navigateToCrmMonthArchive, navigateToCrmYearArchive = navigateToCrmYearArchive)
    }

    composable(route = NavigationScreens.CRM_MONTH.name) {
      CrmArchiveMonthScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_YEAR.name) {
      CrmArchiveYearScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_CLIENT_LIST.name) {
      ClientListScreen(onBack = { navController.popBackStack() }, navigateToCrmClientDetails = navigateToCrmClientDetails, navigateCrmClientAdd = navigateCrmClientAdd)
    }

    composable(route = NavigationScreens.CRM_CLIENT_DETAILS.name) {
      CrmClientDetails(onBack = { navController.popBackStack() }, navigateCrmOrderList = navigateCrmOrderList, navigateCrmOrderDetails = navigateCrmOrderDetails, navigateCrmOrderAdd = navigateCrmOrderAdd)
    }

    composable(route = NavigationScreens.CRM_ORDER_LIST.name) {
      CrmOrderListScreen(onBack = { navController.popBackStack() }, navigateCrmOrderDetails = navigateCrmOrderDetails)
    }

    composable(route = NavigationScreens.CRM_ORDER_DETAIL.name) {
      CrmOrderDetailsScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_CLIENT_ADD.name) {
      CrmClientAdd(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_ORDER_ADD.name) {
      CrmOrderAdd(onBack = { navController.popBackStack() })
    }
  }
}
