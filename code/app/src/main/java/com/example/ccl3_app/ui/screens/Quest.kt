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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp

@Composable
fun QuestScreen(
    viewModel: QuestViewModel = viewModel()
) {
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
                .background(Teal)
                .padding(vertical = 24.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "Seasonal Quests",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
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
            // Empty state
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
            // Progress Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightTeal.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Quests completed",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
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
                                    .background(Orange)
                            )
                        }

                        // Progress text
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$completedCount/$totalQuests",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
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
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    quests.forEachIndexed { index, quest ->
                        val isCurrentQuest = !quest.isDone &&
                                (index == 0 || quests.getOrNull(index - 1)?.isDone == true)

                        QuestNode(
                            quest = quest,
                            isCurrentQuest = isCurrentQuest,
                            onClick = {
                                if (isCurrentQuest) {
                                    selectedQuest = quest
                                }
                            },
                            // Alternate left/right positioning
                            offsetX = if (index % 2 == 0) (-40).dp else 40.dp
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
            }
        )
    }
}

@Composable
fun QuestNode(
    quest: Quest,
    isCurrentQuest: Boolean,
    onClick: () -> Unit,
    offsetX: Dp = 0.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    // Pulsing animation for current quest
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Pulsing ring for current quest
        if (isCurrentQuest) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .border(
                        width = 3.dp,
                        color = Orange.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            )
        }

        // Quest circle
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(
                    when {
                        quest.isDone -> Orange
                        isCurrentQuest -> Orange
                        else -> Color.LightGray
                    }
                )
                .clickable(enabled = isCurrentQuest) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            // Yellow indicator for current quest
            if (isCurrentQuest && !quest.isDone) {
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .offset(y = (-15).dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 4.dp,
                                topEnd = 4.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )
                        )
                        .background(PostItYellow)
                )
            }
        }
    }
}

@Composable
fun QuestDetailDialog(
    quest: Quest,
    onDismiss: () -> Unit,
    onStart: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(16.dp)
                .clickable(onClick = {}), // Prevent dismissing when clicking card
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = PostItYellow
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quest circle at top
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Orange),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = (-20).dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 4.dp,
                                    topEnd = 4.dp,
                                    bottomStart = 0.dp,
                                    bottomEnd = 0.dp
                                )
                            )
                            .background(PostItYellow)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = "Level ${quest.level}: ${quest.title}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // Description
                Text(
                    text = quest.description,
                    fontSize = 16.sp,
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Start Button
                Button(
                    onClick = onStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Start",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}