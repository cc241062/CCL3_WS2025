package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.ui.theme.*
import com.example.ccl3_app.ui.viewmodels.HomeViewModel
import com.example.ccl3_app.ui.components.RecipeCard
import com.example.ccl3_app.ui.components.NewQuestsCard
import com.example.ccl3_app.ui.components.WelcomeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyRecipeCard(
    onAddRecipe: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = PostItYellow
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Empty state icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📝",
                    fontSize = 72.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "No Recipes Yet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Description
            Text(
                text = "Start your cooking journey by creating your first recipe!",
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add Recipe Button
            Button(
                onClick = onAddRecipe,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Recipe",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Your First Recipe",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onOpenProfiles: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onAddRecipe: (Int) -> Unit = {},
    onNavigateToQuests: () -> Unit = {}  // ADD THIS
) {
    val featuredRecipes by viewModel.featuredRecipes.collectAsState()
    val currentRecipeIndex by viewModel.currentRecipeIndex.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        /*Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Teal)
                .padding(vertical = 24.dp, horizontal = 20.dp)
        ) {
            Text(
                text = "Oops! I can cook",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }*/

        Spacer(modifier = Modifier.height(16.dp))

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Card
            WelcomeCard()

            // New Quests Card
            NewQuestsCard(onClick = onNavigateToQuests)

            // Recipe Card
            val currentRecipe = featuredRecipes.getOrNull(currentRecipeIndex)
            if (currentRecipe != null) {
                RecipeCard(
                    recipe = currentRecipe,
                    onRecipeClick = { onRecipeClick(currentRecipe.id) },
                    onPreviousClick = { viewModel.previousRecipe() },
                    onNextClick = { viewModel.nextRecipe() }
                )
            } else {
                // No recipes available - Show placeholder card
                EmptyRecipeCard(
                    onAddRecipe = { onAddRecipe(1) }
                )
            }
        }
    }
}