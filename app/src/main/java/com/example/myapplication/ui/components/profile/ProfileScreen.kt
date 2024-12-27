package com.example.myapplication.ui.components.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.utils.LocalStorage
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  onBack: () -> Unit,
  onLogout: () -> Unit,
  navigateToCrmMain: () -> Unit,
  editProfile: () -> Unit
) {
  val context = LocalContext.current

  val user = LocalStorage.getUser(context)
  var isLoggingOut by remember { mutableStateOf(false) }

  if (isLoggingOut) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      CircularProgressIndicator()
    }

    LaunchedEffect(Unit) {
      delay(500)
      onLogout()
    }
  } else {
    Scaffold(containerColor = Color(0xFFFDF8ED),
      topBar = {
        TopAppBar(
          title = { Text("Мій кабінет", color = Color.White) },
          navigationIcon = {
            IconButton(onClick = onBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = Color.White)
            }
          },
          colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF583E23))
        )
      }
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Column(modifier = Modifier.fillMaxWidth()) {
          Button(
            onClick = editProfile,
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF583E23), // Колір фону кнопки
              contentColor = Color.White         // Колір тексту кнопки
            ),
          ) {
            Text("Редагувати профіль")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
              Icons.Filled.ChevronRight,
              contentDescription = "Редагувати профіль",
              modifier = Modifier.size(24.dp)
            )
          }

          if (user?.isEmployee == true) {
            Spacer(modifier = Modifier.height(8.dp))

            Button(
              onClick = navigateToCrmMain,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),

              shape = RoundedCornerShape(4.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF583E23), // Колір фону кнопки
                contentColor = Color.White         // Колір тексту кнопки
              ),
            ) {
              Text("До CRM")
              Spacer(modifier = Modifier.weight(1f))
              Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "До CRM",
                modifier = Modifier.size(24.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Button(
            onClick = { isLoggingOut = true },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = Color(0xFF583E23), // Колір фону кнопки
              contentColor = Color.White         // Колір тексту кнопки
            ),
          ) {
            Text("Вийти")
            Spacer(modifier = Modifier.weight(1f))
            Icon(
              Icons.Filled.ChevronRight,
              contentDescription = "Вийти",
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    }
  }
}
