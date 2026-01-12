package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onOpenProfiles: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onAddRecipe: (Int) -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Home Screen")
        Button(onClick = onOpenProfiles) {
            Text("Open Profiles")
        }
        Button(onClick = { onRecipeClick(1) }) {
            Text("Go to Recipe Detail (id=1)")
        }
        Button(onClick = { onAddRecipe(1) }) {
            Text("Add Recipe (stackId=1)")
        }
    }
}
