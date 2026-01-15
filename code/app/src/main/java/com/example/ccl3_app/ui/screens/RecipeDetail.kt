package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    onEdit: (Int) -> Unit = {},  // ← ADD THIS LINE
    viewModel: RecipeDetailViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
    val recipe by viewModel.recipe.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

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
            Text(
                text = recipe?.title ?: "Recipe name",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Row {
                IconButton(onClick = { onEdit(recipeId) }) {  // ← CHANGE THIS LINE
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { recipe?.let { viewModel.deleteRecipe(it) } }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                }
            }
        }

        // ... rest of your code stays the same

        Spacer(modifier = Modifier.height(16.dp))

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

                Text("Ingredients:", fontWeight = FontWeight.Bold, color = Teal)

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(recipe?.description ?: "Description…")
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous")
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                    }
                }
            }
        }
    }
}
