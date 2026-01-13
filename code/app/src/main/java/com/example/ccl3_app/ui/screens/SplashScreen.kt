package com.example.ccl3_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ccl3_app.ui.theme.Orange
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Scale animation for the entire text
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        ),
        label = "scale"
    )

    // Scale animation for the "O" - starts after text begins shrinking
    val oScale by animateFloatAsState(
        targetValue = if (startAnimation) 8f else 1f,
        animationSpec = tween(
            durationMillis = 1500,
            delayMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = "oScale"
    )

    LaunchedEffect(key1 = true) {
        delay(500) // Wait a bit before starting
        startAnimation = true
        delay(2200) // Total animation time + buffer
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Orange),
        contentAlignment = Alignment.Center
    ) {
        if (scale > 0.1f) {
            // Show the full text while it's visible
            Text(
                text = "Oops! I can cook",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.scale(scale)
            )
        } else {
            // Show just the "O" when text disappears
            Text(
                text = "O",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.scale(oScale)
            )
        }
    }
}