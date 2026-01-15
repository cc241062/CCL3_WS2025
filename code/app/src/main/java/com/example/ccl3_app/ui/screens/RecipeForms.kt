package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.viewmodels.RecipeFormsViewModel
import com.example.ccl3_app.ui.viewmodels.ViewModelProvider

@Composable
fun RecipeFormScreen(
    stackId: Int?,
    recipeId: Int?,
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: RecipeFormsViewModel =
        viewModel(factory = ViewModelProvider.Factory)
) {

    LaunchedEffect(recipeId) {
        recipeId?.let { viewModel.loadRecipe(it) }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = if (recipeId == null) "Create Recipe" else "Edit Recipe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = viewModel.ingredients,
            onValueChange = { viewModel.ingredients = it },
            label = { Text("Ingredients (one per line)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        OutlinedTextField(
            value = viewModel.instructions,
            onValueChange = { viewModel.instructions = it },
            label = { Text("Instructions (one per line)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Button(
            onClick = {
                //viewModel.saveRecipe()
                onDone()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Orange)
        ) {
            Text("Save Recipe")
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.instructions,
            onValueChange = { viewModel.instructions = it },
            label = { Text("Test Test Test") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )
    }
}
