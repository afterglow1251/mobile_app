package com.example.myapplication.ui.components.ui

import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.squareup.picasso.Picasso

@Composable
fun PicassoImage(url: String, modifier: Modifier = Modifier) {
  AndroidView(
    modifier = modifier,
    factory = { context ->
      ImageView(context).apply {
        Picasso.get()
          .load(url)
          .into(this) // Завантажуємо зображення за допомогою Picasso
      }
    }
  )
}