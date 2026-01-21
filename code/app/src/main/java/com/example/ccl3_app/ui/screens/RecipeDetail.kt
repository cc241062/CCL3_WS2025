package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.R
import com.example.ccl3_app.ui.viewmodels.RecipeDetailViewModel
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.theme.LightTeal
import com.example.ccl3_app.ui.theme.Jua
import com.example.ccl3_app.ui.viewmodels.ViewModelProvider
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onBack: () -> Unit = {},
    onEdit: (Int) -> Unit = {},
    viewModel: RecipeDetailViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
    val recipe by viewModel.recipe.collectAsState()
    var currentCardIndex by remember { mutableStateOf(0) } // 0 = ingredients, 1+ = instruction steps

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    // Total cards = 1 (ingredients) + number of instructions
    val totalCards = 1 + (recipe?.instructions?.size ?: 0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {

        // HEADER ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE37434))
                .padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // Title
            Text(
                text = recipe?.title ?: "Recipe name",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Edit + Delete buttons with FIT so they don't crop
            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = { onEdit(recipeId) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit recipe",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp),
                    )
                }

                IconButton(onClick = {
                    recipe?.let {
                        viewModel.deleteRecipe(it)
                        onBack()
                    }
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.delete),
                        contentDescription = "Delete recipe",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp),
                    )
                }
            }
        }


        Spacer(modifier = Modifier.height(8.dp))

        // 🔽 CONTENT AREA
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // ----------------------------
            // DESCRIPTION TITLE + CONTENT
            // ----------------------------

            Text(
                text = "Description",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = recipe?.description ?: "No description available.",
                fontSize = 15.sp,
                fontFamily = Jua,
                color = Color.DarkGray,
                lineHeight = 20.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
            Divider(color = Color(0xFFE0E0E0), thickness = 2.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // ----------------------------
            // RESPONSIVE RECIPE CARD
            // ----------------------------

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),   // takes all remaining space
                contentAlignment = Alignment.TopCenter           // ⬅ moves card UP
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    val cardHeight = maxHeight * 0.65f   // slightly taller now

                    // Offset shadow card behind
                    Box(
                        modifier = Modifier
                            .offset(10.dp, 10.dp)
                            .fillMaxWidth()
                            .height(cardHeight)
                            .background(Teal.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                    )

                    // Front card
                    var totalDragX by remember { mutableStateOf(0f) }
                    val swipeThreshold = 80f  // px – adjust if needed

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                            .shadow(8.dp, RoundedCornerShape(20.dp))
                            .background(LightTeal, RoundedCornerShape(20.dp))
                            .pointerInput(totalCards, currentCardIndex) {
                                detectHorizontalDragGestures(
                                    onHorizontalDrag = { change, dragAmount ->
                                        totalDragX += dragAmount
                                        change.consume()
                                    },
                                    onDragEnd = {
                                        // swipe RIGHT → previous card
                                        if (totalDragX > swipeThreshold && currentCardIndex > 0) {
                                            currentCardIndex--
                                        }
                                        // swipe LEFT → next card
                                        else if (totalDragX < -swipeThreshold && currentCardIndex < totalCards - 1) {
                                            currentCardIndex++
                                        }
                                        totalDragX = 0f
                                    },
                                    onDragCancel = {
                                        totalDragX = 0f
                                    }
                                )
                            }
                            .padding(16.dp)
                    ) {

                        // Section Title
                        Text(
                            text = if (currentCardIndex == 0) "Ingredients:" else "Step $currentCardIndex:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = Jua,
                            color = Teal
                        )

                        Spacer(Modifier.height(12.dp))

                        // Content Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .background(
                                    Color.White.copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            if (currentCardIndex == 0) {
                                Column {
                                    recipe?.ingredients?.forEach { ingredient ->
                                        Text(
                                            text = "• $ingredient",
                                            fontSize = 16.sp,
                                            fontFamily = Jua,
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            color = Color.Black
                                        )
                                    }
                                    if (recipe?.ingredients.isNullOrEmpty()) {
                                        Text(
                                            "No ingredients listed",
                                            fontFamily = Jua,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            } else {
                                val idx = currentCardIndex - 1
                                recipe?.instructions?.getOrNull(idx)?.let { step ->
                                    Text(
                                        text = step,
                                        fontSize = 16.sp,
                                        fontFamily = Jua,
                                        lineHeight = 24.sp,
                                        color = Color.Black
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Nav buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // PREVIOUS BUTTON (left arrow)
                            IconButton(
                                onClick = { if (currentCardIndex > 0) currentCardIndex-- },
                                enabled = currentCardIndex > 0
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_l_dark),
                                    contentDescription = "Previous step",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

// PROGRESS TEXT (unchanged)
                            Text(
                                text = "${currentCardIndex + 1} / $totalCards",
                                fontSize = 14.sp,
                                fontFamily = Jua,
                                color = Teal,
                                fontWeight = FontWeight.SemiBold
                            )

// NEXT BUTTON (right arrow)
                            IconButton(
                                onClick = { if (currentCardIndex < totalCards - 1) currentCardIndex++ },
                                enabled = currentCardIndex < totalCards - 1
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_r_dark),
                                    contentDescription = "Next step",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}