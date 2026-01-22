package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ccl3_app.R
import kotlinx.coroutines.launch

// Data class for one step in the quest
data class FryEggStep(
    val title: String,
    val body: String,
    val imageRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FryEggQuestScreen(
    fontFamily: FontFamily,
    onClose: () -> Unit,      // X button
    onFinished: () -> Unit    // Finish button on last step
) {
    // Steps with images
    val steps = listOf(
        FryEggStep(
            title = "Overview",
            body = "In this quest you’ll learn how to fry a sunny-side-up egg safely. " +
                    "You’ll prepare your tools, heat the pan, and cook the egg without burning it.",
            imageRes = R.drawable.egg_overview
        ),
        FryEggStep(
            title = "Step 1 – Get your tools ready",
            body = "You’ll need: a non-stick pan, a spatula, a small bowl, some oil or butter, " +
                    "and one egg. Place everything close to the stove so you don’t have to reach over heat later.",
            imageRes = R.drawable.egg_step1
        ),
        FryEggStep(
            title = "Step 2 – Heat the pan",
            body = "Place the pan on medium heat. Add about 1 teaspoon of oil or a small knob of butter. " +
                    "Wait until the fat is melted and gently shimmering, but not smoking.",
            imageRes = R.drawable.egg_step2
        ),
        FryEggStep(
            title = "Step 3 – Crack & cook the egg",
            body = "Crack the egg into a small bowl first, then gently slide it into the pan. " +
                    "Cook until the white is set and the yolk is still soft (about 2–3 minutes). " +
                    "Turn off the heat and use the spatula to lift the egg onto a plate.",
            imageRes = R.drawable.egg_step3
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { steps.size }
    )
    val scope = rememberCoroutineScope()

    val page = pagerState.currentPage
    val pageCount = steps.size
    val progress = (page + 1f) / pageCount.toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {

        // ─── Top bar: X + progress ───
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF305B63)) // dark teal track
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(Color(0xFFD9FFF2)) // mint fill
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ─── Card block with pager ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // Back shadow card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .fillMaxHeight()
                    .offset(x = 16.dp, y = 12.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFFAA5423)) // darker orange shadow
            )

            // Front card with pager
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE37434) // orange front card
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { pageIndex ->
                    val step = steps[pageIndex]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = step.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = fontFamily,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Image for this step
                        Image(
                            painter = painterResource(id = step.imageRes),
                            contentDescription = step.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1.0f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = step.body,
                            fontSize = 16.sp,
                            fontFamily = fontFamily,
                            color = Color.White,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ─── Bottom Next / Finish button ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Shadow behind button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(52.dp)
                    .offset(y = 4.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF0A3941)) // dark teal shadow
            )

            val isLastPage = page == pageCount - 1
            val buttonText = if (isLastPage) "Finish" else "Next"

            Button(
                onClick = {
                    if (isLastPage) {
                        onFinished()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4B9DA9)
                )
            ) {
                Text(
                    text = buttonText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    color = Color.White
                )
            }
        }
    }
}
