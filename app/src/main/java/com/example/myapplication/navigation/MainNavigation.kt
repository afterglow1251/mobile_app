package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.components.products.ProductListScreen
import com.example.myapplication.utils.LocalStorage
import androidx.compose.ui.platform.LocalContext
import com.example.myapplication.ui.components.auth.AuthScreen

import com.example.myapplication.ui.components.cart.CartScreen
import com.example.myapplication.ui.components.crm.ClientListScreen
import com.example.myapplication.ui.components.crm.CrmArchiveMonthScreen
import com.example.myapplication.ui.components.crm.CrmArchiveScreen
import com.example.myapplication.ui.components.crm.CrmArchiveYearScreen
import com.example.myapplication.ui.components.crm.CrmClientAdd
import com.example.myapplication.ui.components.crm.CrmClientDetails
import com.example.myapplication.ui.components.crm.CrmClientEdit

import com.example.myapplication.ui.components.crm.CrmMainScreen
import com.example.myapplication.ui.components.crm.CrmOrderAdd
import com.example.myapplication.ui.components.crm.CrmOrderDetailsScreen

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
    LocalStorage.logoutUser(context)
    navController.navigate(NavigationScreens.AUTH.name) {
      popUpTo(0)
    }
  }


  val navigateToCrmMain: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_MAIN.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmStats: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_STATS.name) {
        launchSingleTop = true
      }
    }
  }


  val navigateToCrmArchive: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_ARCHIVE.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmMonthArchive: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_MONTH.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmYearArchive: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_YEAR.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmClientList: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_CLIENT_LIST.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmClientDetails: (Int) -> Unit = { customerId ->
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate("${NavigationScreens.CRM_CLIENT_DETAILS.name}/$customerId") {
        launchSingleTop = true
      }
    }
  }

  val navigateCrmOrderList: (Int) -> Unit = { customerId ->
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate("${NavigationScreens.CRM_ORDER_LIST.name}/$customerId") {
        launchSingleTop = true
      }
    }
  }

  val navigateCrmOrderDetails: (Int) -> Unit = { orderId ->
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate("${NavigationScreens.CRM_ORDER_DETAIL.name}/$orderId") {
        launchSingleTop = true
      }
    }
  }

  val navigateCrmClientAdd: () -> Unit = {
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate(NavigationScreens.CRM_CLIENT_ADD.name) {
        launchSingleTop = true
      }
    }
  }

  val navigateCrmOrderAdd: (Int) -> Unit = { customerId ->
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate("${NavigationScreens.CRM_ORDER_ADD.name}/$customerId") {
        launchSingleTop = true
      }
    }
  }

  val navigateToCrmClientEdit: (Int) -> Unit = { clientId ->
    val user = LocalStorage.getUser(context)
    if (user?.isEmployee == true) {
      navController.navigate("${NavigationScreens.CRM_CLIENT_EDIT.name}/$clientId") {
        launchSingleTop = true
      }
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
      ProfileScreen(
        onBack = { navController.popBackStack() },
        onLogout = onLogout,
        editProfile = editProfile,
        navigateToCrmMain = navigateToCrmMain
      )
    }

    composable(route = NavigationScreens.ORDERS.name) {
      OrderListScreen(
        cartDetails = cartDetails,
        showMain = navigateToProductsList,
        onShowOrderDetails = showOrderDetails
      )
    }

    composable(route = NavigationScreens.PROFILE_EDIT.name) {
      EditProfileScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.SEARCH_SCREEN.name) {
      SearchScreen(
        onBack = { navController.popBackStack() },
        showProductDetails = showProductDetails,
      )
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
        CartScreen(
          userId = userId,
          onBack = { navController.popBackStack() },
          showProductDetails = showProductDetails
        )
      }
    }

    composable(route = "${NavigationScreens.ORDER_DETAILS.name}/{orderId}") { backStackEntry ->
      val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
      if (orderId != null) {
        OrderDetailsScreen(orderId = orderId, onBack = { navController.popBackStack() })
      }
    }


    composable(route = NavigationScreens.CRM_MAIN.name) {
      CrmMainScreen(
        onBack = { navController.popBackStack() },
        navigateToCrmStats = navigateToCrmStats,
        navigateToCrmClientList = navigateToCrmClientList
      )
    }

    composable(route = NavigationScreens.CRM_STATS.name) {
      CrmStatsScreen(
        onBack = { navController.popBackStack() },
        navigateToCrmArchive = navigateToCrmArchive
      )
    }

    composable(route = NavigationScreens.CRM_ARCHIVE.name) {
      CrmArchiveScreen(
        onBack = { navController.popBackStack() },
        navigateToCrmMonthArchive = navigateToCrmMonthArchive,
        navigateToCrmYearArchive = navigateToCrmYearArchive
      )
    }

    composable(route = NavigationScreens.CRM_MONTH.name) {
      CrmArchiveMonthScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_YEAR.name) {
      CrmArchiveYearScreen(onBack = { navController.popBackStack() })
    }

    composable(route = NavigationScreens.CRM_CLIENT_LIST.name) {
      ClientListScreen(
        onBack = { navController.popBackStack() },
        navigateToCrmClientDetails = navigateToCrmClientDetails,
        navigateCrmClientAdd = navigateCrmClientAdd
      )
    }

    composable(route = "${NavigationScreens.CRM_CLIENT_DETAILS.name}/{clientId}") { backStackEntry ->
      val clientId = backStackEntry.arguments?.getString("clientId")?.toIntOrNull()
      clientId?.let {
        CrmClientDetails(
          clientId = it,
          onBack = { navController.popBackStack() },
          navigateCrmOrderList = navigateCrmOrderList,
          navigateCrmOrderDetails = navigateCrmOrderDetails,
          navigateCrmOrderAdd = navigateCrmOrderAdd,
          navigateToCrmClientEdit = navigateToCrmClientEdit
        )
      }
    }


    composable(route = "${NavigationScreens.CRM_ORDER_LIST.name}/{customerId}") { backStackEntry ->
      val customerId = backStackEntry.arguments?.getString("customerId")?.toIntOrNull()
      if (customerId != null) {
        CrmOrderListScreen(
          onBack = { navController.popBackStack() },
          navigateCrmOrderDetails = navigateCrmOrderDetails,
          customerId = customerId
        )
      }
    }

    composable(route = "${NavigationScreens.CRM_ORDER_DETAIL.name}/{orderId}")
    { backStackEntry ->
      val orderId = backStackEntry.arguments?.getString("orderId")?.toIntOrNull()
      if (orderId != null) {
        CrmOrderDetailsScreen(
          orderId = orderId,
          onBack = { navController.popBackStack() }
        )
      }
    }


    composable(route = NavigationScreens.CRM_CLIENT_ADD.name) {
      CrmClientAdd(onBack = { navController.popBackStack() })
    }

    composable(route = "${NavigationScreens.CRM_ORDER_ADD.name}/{customerId}") { backStackEntry ->
      val customerId = backStackEntry.arguments?.getString("customerId")?.toIntOrNull()
      if (customerId != null) {
        CrmOrderAdd(
          customerId = customerId,
          onBack = { navController.popBackStack() }
        )
      }
    }


    composable(route = "${NavigationScreens.CRM_CLIENT_EDIT.name}/{clientId}") { backStackEntry ->
      val clientId = backStackEntry.arguments?.getString("clientId")?.toIntOrNull()
      if (clientId != null) {
        CrmClientEdit(
          clientId = clientId,
          onBack = { navController.popBackStack() }
        )
      }
    }
  }
}


