package com.example.ccl3_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ccl3_app.R

@Composable
fun QuestFinishScreen(
    fontFamily: FontFamily,
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A3941)),
        contentAlignment = Alignment.Center
    ) {
        // MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Mascot on top
            MascotCelebration(
                bodyRes = R.drawable.body,
                eyeRes = R.drawable.eye,
                mouthRes = R.drawable.mouth
            )

            // Title
            Text(
                text = "Are you a master cook ?",
                color = Color(0xFFE37434),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily
            )

            // Subtitle
            Text(
                text = "Quest complete",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // shadow
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp)
                        .offset(y = 4.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF06262C))
                )

                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4B9DA9)
                    )
                ) {
                    Text(
                        text = "Continue",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily
                    )
                }
            }
        }

        ConfettiBurst(
            confettiRes = R.drawable.confetti
        )
    }
}

@Composable
private fun MascotCelebration(
    bodyRes: Int,
    eyeRes: Int,
    mouthRes: Int
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascotAnim")


    val bobOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bob"
    )


    val blinkScaleY by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2600
                1f at 0
                1f at 1500

                // blink
                0.15f at 1550
                1f at 1600

                // second blink
                0.15f at 2000
                1f at 2100
            }
        ),
        label = "blink"
    )

    Box(
        modifier = Modifier
            .size(width = 140.dp, height = 220.dp)
            .graphicsLayer(translationY = bobOffset),
        contentAlignment = Alignment.TopCenter
    ) {
        // BODY
        Image(
            painter = painterResource(id = bodyRes),
            contentDescription = "Mascot body",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.graphicsLayer(
                    scaleY = blinkScaleY
                )
            ) {
                Image(
                    painter = painterResource(id = eyeRes),
                    contentDescription = "Left eye",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )

                Image(
                    painter = painterResource(id = eyeRes),
                    contentDescription = "Right eye",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = mouthRes),
                contentDescription = "Mascot mouth",
                modifier = Modifier.size(30.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}



@Composable
private fun ConfettiBurst(
    confettiRes: Int
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val offsetX = remember { Animatable(120f) }

    LaunchedEffect(Unit) {
        scale.snapTo(0.4f)
        alpha.snapTo(0f)
        offsetX.snapTo(160f)

        // pop + slide in
        alpha.animateTo(1f, tween(200))
        offsetX.animateTo(0f, tween(350, easing = FastOutSlowInEasing))
        scale.animateTo(1.35f, tween(220))
        scale.animateTo(1f, tween(160))

        // hold on screen
        kotlinx.coroutines.delay(850)

        // fade out slowly
        alpha.animateTo(0f, tween(600))
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val confettiSize = 150.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-40).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // LEFT CONFETTI
            Image(
                painter = painterResource(id = confettiRes),
                contentDescription = "Confetti",
                modifier = Modifier
                    .size(confettiSize)
                    .graphicsLayer(
                        translationX = -offsetX.value,
                        scaleX = scale.value,
                        scaleY = scale.value,
                        alpha = alpha.value
                    ),
                contentScale = ContentScale.Fit
            )

            // RIGHT CONFETTI
            Image(
                painter = painterResource(id = confettiRes),
                contentDescription = "Confetti mirrored",
                modifier = Modifier
                    .size(confettiSize)
                    .graphicsLayer(
                        translationX = offsetX.value,
                        scaleX = -scale.value,
                        scaleY = scale.value,
                        alpha = alpha.value
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}

