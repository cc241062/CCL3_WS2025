package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.ccl3_app.ui.theme.CardBackground

@Composable
fun RecipeDetailScreen(
    recipeId: Int,
    onBack: () -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Recipe Detail Screen")
        Text("Recipe ID: $recipeId")
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
