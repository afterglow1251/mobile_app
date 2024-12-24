package com.example.myapplication.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.LocalStorage

@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit, navigateToCrmMain : () -> Unit, editProfile: () -> Unit) {
  val context = LocalContext.current
  val user = LocalStorage.getUser(context)


  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Профіль користувача",
      fontSize = 24.sp,
      fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = {
      onLogout()
    }) {
      Text("Вийти")
    }
    Spacer(modifier = Modifier.height(16.dp))
    if (user?.isEmployee == true) {
      Button(onClick = {
        navigateToCrmMain()
      }) {
        Text("До CRM")
      }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Button(onClick = {
      editProfile()
    }) {
      Text("Редагувати профіль")
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = onBack) {
      Text("Повернутися на головну")
    }
  }
}
