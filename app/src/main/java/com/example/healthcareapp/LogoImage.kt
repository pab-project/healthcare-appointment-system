package com.example.healthcareapp

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun LogoImage(
    drawableResId: Int,
    sizeDp: Int = 100,
    paddingDp: Int = 8
) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            android.widget.ImageView(ctx).apply {
                setImageDrawable(ContextCompat.getDrawable(ctx, drawableResId))
                scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
            }
        },
        update = { view ->
            view.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
        },
        modifier = Modifier
            .size(sizeDp.dp)
            .padding(paddingDp.dp)
    )
}