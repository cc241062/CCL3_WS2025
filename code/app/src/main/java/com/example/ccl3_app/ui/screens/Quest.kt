package com.example.ccl3_app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.Quest
import com.example.ccl3_app.ui.theme.*
import com.example.ccl3_app.ui.viewmodels.QuestViewModel

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import com.example.ccl3_app.R
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.PathEffect



@Composable
fun QuestScreen(
    viewModel: QuestViewModel = viewModel()
) {
    val juaFont = FontFamily(Font(R.font.jua_regular))

    val quests by viewModel.quests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedQuest by remember { mutableStateOf<Quest?>(null) }

    val completedCount = quests.count { it.isDone }
    val totalQuests = quests.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(QuestHeader)
                .padding(vertical = 28.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "January Quests",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = juaFont,
                color = Color.White
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Orange)
            }
        } else if (quests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No quests available yet!",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {


            // Progress Card with shadow behind
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // BACK SHADOW CARD
                Card(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(y = 6.dp),                    // slight drop shadow effect
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1C393D)    // same dark teal as other shadows
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {}

                // FRONT PROGRESS CARD (your original one)
                Card(
                    modifier = Modifier,
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightTeal
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Quests completed:",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = juaFont,
                            color = Color(0xFF0A3941)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Teal)
                        ) {
                            if (totalQuests > 0) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(completedCount.toFloat() / totalQuests)
                                        .background(Color(0xFFE37434)) // your orange
                                )
                            }

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$completedCount/$totalQuests",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = juaFont,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }





            // Quest Path
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    quests.forEachIndexed { index, quest ->
                        val offsetX = if (index % 2 == 0) (-50).dp else 50.dp
                        val prevOffsetX = if (index == 0) null else {
                            if ((index - 1) % 2 == 0) (-50).dp else 50.dp
                        }

                        // ✅ draw connector only if there is a previous node
                        if (prevOffsetX != null) {
                            OrganicQuestPathConnector(
                                fromOffsetX = prevOffsetX,
                                toOffsetX = offsetX,
                                index = index,
                                isDone = quests[index - 1].isDone  // ✅ segment becomes solid when the previous quest is done
                            )
                        }


                        QuestNode(
                            quest = quest,
                            isCurrentQuest = !quest.isDone &&
                                    (index == 0 || quests.getOrNull(index - 1)?.isDone == true),
                            onClick = {
                                if (!quest.isDone) selectedQuest = quest
                            },
                            offsetX = offsetX,
                            fontFamily = juaFont
                        )
                    }

                }
            }
        }
    }

    // Quest Detail Dialog
    selectedQuest?.let { quest ->
        QuestDetailDialog(
            quest = quest,
            onDismiss = { selectedQuest = null },
            onStart = {
                viewModel.completeQuest(quest)
                selectedQuest = null
            },
            fontFamily = juaFont
        )
    }
}


@Composable
fun OrganicQuestPathConnector(
    fromOffsetX: Dp,
    toOffsetX: Dp,
    index: Int,
    isDone: Boolean
) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
    ) {
        val w = size.width
        val h = size.height

        val fromX = w / 2f + fromOffsetX.toPx()
        val toX = w / 2f + toOffsetX.toPx()

        // organic wobble
        val wobble = (if (index % 2 == 0) 1 else -1) * 22.dp.toPx()
        val midY = h / 2f

        val c1 = Offset(fromX + wobble, midY * 0.3f)
        val c2 = Offset(toX - wobble, midY * 1.7f)

        val path = Path().apply {
            moveTo(fromX, 0f)
            cubicTo(
                c1.x, c1.y,
                c2.x, c2.y,
                toX, h
            )
        }

        val stroke = Stroke(
            width = 12.dp.toPx(),                 // thinner line
            cap = StrokeCap.Round,
            pathEffect = if (isDone) null else PathEffect.dashPathEffect(
                floatArrayOf(
                    4.dp.toPx(),                  // short dash
                    25.dp.toPx()                  // space
                ),
                0f
            )
        )

        drawPath(
            path = path,
            color = if (isDone) Color(0xFFE37434) else Color(0xFFE5E5E5),
            style = stroke
        )
    }
}


@Composable
fun QuestNode(
    quest: Quest,
    isCurrentQuest: Boolean,
    onClick: () -> Unit,
    offsetX: androidx.compose.ui.unit.Dp,
    fontFamily: FontFamily
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.offset(x = offsetX),
            contentAlignment = Alignment.Center
        ) {
            // Pulsing ring for current quest
            if (isCurrentQuest) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .scale(pulseScale)
                        .border(
                            width = 3.dp,
                            color = Orange.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                )
            }

            // Colors based on state
            val circleColor = if (quest.isDone || isCurrentQuest) Color(0xFFE37434) else Color(0xFFD0D0D0)
            val shadowColor = if (quest.isDone || isCurrentQuest) Color(0xFF9B5A2D) else Color(0xFFB0B0B0)

            // Shadow disc
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .offset(y = 5.dp)
                    .clip(CircleShape)
                    .background(shadowColor)
            )

            // Main quest circle
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(circleColor)
                    .clickable(enabled = isCurrentQuest && !quest.isDone) { onClick() },
                contentAlignment = Alignment.Center
            ) {
                // Display level number for completed and current quests
                if (quest.isDone || isCurrentQuest) {
                    Text(
                        text = "${quest.level}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        color = Color.White
                    )
                }
            }

                // Triangle marker pointing DOWN at current quest
                if (isCurrentQuest && !quest.isDone) {
                    DownTriangleMarker(
                        modifier = Modifier
                            .width(44.dp)
                            .height(30.dp)
                            .offset(y = (-32).dp),
                        color = Color(0xFFFEE22B)
                    )
            }
        }
    }
}

@Composable
fun DownTriangleMarker(
    modifier: Modifier = Modifier,
    color: Color
) {
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w / 2f, h)    // bottom point (pointing down)
            lineTo(0f, 0f)       // top left
            lineTo(w, 0f)        // top right
            close()
        }
        drawPath(path = path, color = color)
    }
}

@Composable
fun QuestDetailDialog(
    quest: Quest,
    onDismiss: () -> Unit,
    onStart: () -> Unit,
    fontFamily: FontFamily
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        // Wrap card + circle together so we can float the circle above
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            contentAlignment = Alignment.TopCenter
        ) {
            // Top circle sitting above the card
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .offset(y = (-32).dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Shadow under the circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = 4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7A3A18))
                )

                // Main orange circle
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE37434))
                )

                // Little notch / triangle touching the card
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(22.dp)
                        .offset(y = 10.dp)
                ) {
                    val w = size.width
                    val h = size.height
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w / 2f, 0f)      // top
                        lineTo(0f, h)           // bottom left
                        lineTo(w, h)            // bottom right
                        close()
                    }
                    drawPath(path, color = Color(0xFFE37434))
                }
            }

            // MAIN BROWN CARD
            Card(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .clickable(enabled = false) {}, // block clicks inside
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF9B5A2D) // warm brown
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    // Title
                    Text(
                        text = "Level ${quest.level}: ${quest.title}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Description
                    Text(
                        text = quest.description,
                        fontSize = 16.sp,
                        fontFamily = fontFamily,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Start,
                        lineHeight = 22.sp,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // START BUTTON with little shadow
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // Fake shadow behind the button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(48.dp)
                                .offset(y = 4.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0xFF0A3941)) // dark teal shadow
                        )

                        Button(
                            onClick = onStart,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4B9DA9) // teal
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(48.dp)
                        ) {
                            Text(
                                text = "Start",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = fontFamily,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}