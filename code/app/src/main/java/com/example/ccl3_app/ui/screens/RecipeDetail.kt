package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.ui.viewmodels.RecipeDetailViewModel
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.theme.LightTeal
import com.example.ccl3_app.ui.viewmodels.ViewModelProvider

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
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black
                )
            }

            Text(
                text = recipe?.title ?: "Recipe name",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Row {
                IconButton(onClick = { onEdit(recipeId) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = {
                    recipe?.let {
                        viewModel.deleteRecipe(it)
                        onBack()
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Description
        if (!recipe?.description.isNullOrBlank()) {
            Text(
                text = recipe?.description ?: "",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

            Box(
                modifier = Modifier
                    .offset(10.dp, 10.dp)
                    .fillMaxWidth(0.9f)
                    .height(420.dp)
                    .background(Teal.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(420.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp))
                    .background(LightTeal, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {

                // Section Title
                Text(
                    text = if (currentCardIndex == 0) "Ingredients:"
                    else "Step $currentCardIndex:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Teal
                )

                Spacer(Modifier.height(12.dp))

                // Content Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (currentCardIndex == 0) {
                        // Ingredients Card
                        Column {
                            recipe?.ingredients?.forEach { ingredient ->
                                Text(
                                    text = "• $ingredient",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    color = Color.Black
                                )
                            }
                            if (recipe?.ingredients.isNullOrEmpty()) {
                                Text("No ingredients listed", color = Color.Gray)
                            }
                        }
                    } else {
                        // Instruction Step Card
                        val instructionIndex = currentCardIndex - 1
                        recipe?.instructions?.getOrNull(instructionIndex)?.let { instruction ->
                            Text(
                                text = instruction,
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { if (currentCardIndex > 0) currentCardIndex-- },
                        enabled = currentCardIndex > 0
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Previous",
                            tint = if (currentCardIndex > 0) Color.Black else Color.Gray
                        )
                    }

                    // Progress indicator
                    Text(
                        text = "${currentCardIndex + 1} / $totalCards",
                        fontSize = 14.sp,
                        color = Teal,
                        fontWeight = FontWeight.SemiBold
                    )

                    IconButton(
                        onClick = { if (currentCardIndex < totalCards - 1) currentCardIndex++ },
                        enabled = currentCardIndex < totalCards - 1
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = if (currentCardIndex < totalCards - 1) Color.Black else Color.Gray
                        )
                    }
                }
            }
        }
    }
}