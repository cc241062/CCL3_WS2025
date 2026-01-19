package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.viewmodels.RecipeFormsViewModel
import com.example.ccl3_app.ui.viewmodels.ViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeFormScreen(
    stackId: Int?,
    recipeId: Int?,
    onDone: () -> Unit,
    onBack: () -> Unit,
    viewModel: RecipeFormsViewModel =
        viewModel(factory = ViewModelProvider.Factory)
) {
    val context = LocalContext.current
    val database = OopsDatabase.getDatabase(context)
    val stackRepository = StackRepository(database.StackDao())

    // Load all available stacks
    val allStacks by stackRepository.stacks.collectAsState(initial = emptyList())

    // Selected stack state
    var selectedStackId by remember { mutableStateOf(stackId ?: 1) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        recipeId?.let { viewModel.loadRecipe(it) }
    }

    // Update selectedStackId when recipe loads
    LaunchedEffect(viewModel.stackId) {
        if (recipeId != null) {
            selectedStackId = viewModel.stackId
        }
    }

    LaunchedEffect(allStacks) {
        if (selectedStackId == null && allStacks.isNotEmpty()) {
            selectedStackId = allStacks.first().id
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
            text = if (recipeId == null) "Create Recipe" else "Edit Recipe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        // Stack Selector Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = allStacks.find { it.id == selectedStackId }?.name ?: "Select Stack",
                onValueChange = {},
                readOnly = true,
                label = { Text("Stack") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                allStacks.forEach { stack ->
                    DropdownMenuItem(
                        text = { Text(stack.name) },
                        onClick = {
                            selectedStackId = stack.id
                            expanded = false
                        }
                    )
                }
            }
        }

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
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
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
                viewModel.saveRecipe(selectedStackId?: 1)
                onDone()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Orange),
            enabled = selectedStackId != null
        ) {
            Text("Save Recipe")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}