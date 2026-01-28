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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(400)
        startAnimation = true
        delay(2200)
        onSplashComplete()
    }

    val transition = updateTransition(
        targetState = startAnimation,
        label = "splashTransition"
    )

    val textScale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 900,
                easing = FastOutSlowInEasing
            )
        },
        label = "textScale"
    ) { started ->
        if (!started) 1f else 0.7f
    }

    val textAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 700,
                delayMillis = 300
            )
        },
        label = "textAlpha"
    ) { started ->
        if (!started) 1f else 0f
    }

    val oScale by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 1400,
                delayMillis = 200,
                easing = FastOutSlowInEasing
            )
        },
        label = "oScale"
    ) { started ->
        if (!started) 0f else 8f
    }

    val oAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 900,
                delayMillis = 500
            )
        },
        label = "oAlpha"
    ) { started ->
        if (!started) 0f else 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE37434)),
        contentAlignment = Alignment.Center
    ) {
        // Full text fading / shrinking out
        Text(
            text = "Oops! I can cook",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .scale(textScale)
                .graphicsLayer(alpha = textAlpha)
        )

        // Big “O” zooming / fading in
        Text(
            text = "O",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier
                .scale(oScale)
                .graphicsLayer(alpha = oAlpha)
        )
    }
}
