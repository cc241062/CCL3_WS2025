package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecipeFormScreen(
    stackId: Int,
    onDone: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Recipe Form Screen")
        Text("Stack ID: $stackId")
        Button(onClick = onDone) {
            Text("Done")
        }
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
