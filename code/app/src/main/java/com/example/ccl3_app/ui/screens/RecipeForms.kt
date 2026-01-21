package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.theme.Jua
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

    // All stacks from DB (includes "All recipes")
    val allStacks by stackRepository.stacks.collectAsState(initial = emptyList())

    // Stacks used in THIS form (exclude "All recipes")
    val formStacks = remember(allStacks) {
        allStacks.filter { it.name != "All recipes" }
    }

    var selectedStackId by remember { mutableStateOf<Int?>(stackId) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(recipeId) {
        recipeId?.let { viewModel.loadRecipe(it) }
    }

    // When editing an existing recipe, sync selectedStackId with the ViewModel
    LaunchedEffect(viewModel.stackId, recipeId, formStacks) {
        if (recipeId != null) {
            val vmId = viewModel.stackId
            if (vmId != null && formStacks.any { it.id == vmId }) {
                selectedStackId = vmId
            }
        }
    }

    // Make sure selectedStackId is always a valid, real stack id
    LaunchedEffect(formStacks) {
        if (formStacks.isNotEmpty()) {
            if (selectedStackId == null || formStacks.none { it.id == selectedStackId }) {
                selectedStackId = formStacks.first().id
            }
        }
    }

    val scrollState = rememberScrollState()

    // Name shown in the text field
    val selectedStackName = formStacks.find { it.id == selectedStackId }?.name ?: "Select stack"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top header bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B9DA9))
                .padding(vertical = 16.dp),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = if (recipeId == null) "Create Recipe" else "Edit Recipe",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = Jua,
                modifier = Modifier.align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }

        // Progress Bar
        val totalFields = 4
        val filledFields = listOf(
            viewModel.title.isNotBlank(),
            viewModel.description.isNotBlank(),
            viewModel.ingredients.isNotBlank(),
            viewModel.instructions.isNotBlank()
        ).count { it }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {

            Text(
                text = "Form progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Jua,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFAA5423)) // empty bar color
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(filledFields.toFloat() / totalFields)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFE37434)) // progress color
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$filledFields / $totalFields",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Jua,
                        color = Color.White
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // STACK DROPDOWN LABEL
            Text(
                text = "Stack",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Jua
            )

            // Stack Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedStackName,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = LocalTextStyle.current.copy(fontFamily = Jua),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Teal.copy(alpha = 0.15f),
                        unfocusedContainerColor = Teal.copy(alpha = 0.15f),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Teal
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .border(
                            width = 2.dp,
                            color = Teal.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(22.dp)
                        )
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier
                        .background(Color(0xFFD9F3F3)) // light teal dropdown background
                ) {
                    formStacks.forEach { stack ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stack.name,
                                    fontFamily = Jua,
                                    color = Color(0xFF0A3941)
                                )
                            },
                            onClick = {
                                selectedStackId = stack.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            // TITLE – single line, bright teal
            RecipeField(
                label = "Title",
                value = viewModel.title,
                onValueChange = { viewModel.title = it },
                placeholder = "Recipe name...",
                containerColor = Teal.copy(alpha = 0.16f),
                borderColor = Teal.copy(alpha = 0.90f),
                multiLine = false
            )

            // DESCRIPTION – slightly softer teal
            RecipeField(
                label = "Description",
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                placeholder = "Short description of your recipe...",
                containerColor = Teal.copy(alpha = 0.13f),
                borderColor = Teal.copy(alpha = 0.80f),
                multiLine = true
            )

            // INGREDIENTS – another shade, multi-line
            RecipeField(
                label = "Ingredients (one per line)",
                value = viewModel.ingredients,
                onValueChange = { viewModel.ingredients = it },
                placeholder = "e.g.\n2 eggs\n1 cup milk\n½ tsp salt",
                containerColor = Teal.copy(alpha = 0.10f),
                borderColor = Teal.copy(alpha = 0.70f),
                multiLine = true
            )

            // INSTRUCTIONS – lightest teal, multi-line
            RecipeField(
                label = "Instructions (one per line)",
                value = viewModel.instructions,
                onValueChange = { viewModel.instructions = it },
                placeholder = "Step 1: Preheat oven to 180°C...\nStep 2: Mix ingredients...",
                containerColor = Teal.copy(alpha = 0.08f),
                borderColor = Teal.copy(alpha = 0.60f),
                multiLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val realStackId = selectedStackId?.takeIf { id ->
                        formStacks.any { it.id == id }
                    } ?: formStacks.firstOrNull()?.id

                    if (realStackId != null) {
                        viewModel.saveRecipe(realStackId)
                        onDone()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE37434),
                    contentColor = Color.White
                ),
                enabled = formStacks.isNotEmpty() && selectedStackId != null
            ) {
                Text(
                    text = "Save Recipe",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Jua
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Back",
                    fontFamily = Jua
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun RecipeField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    containerColor: Color,
    borderColor: Color,
    multiLine: Boolean
) {
    Column {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            fontFamily = Jua
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(fontFamily = Jua),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Jua,
                    color = Color.Gray
                )
            },
            singleLine = !multiLine,
            minLines = 1,
            maxLines = if (multiLine) Int.MAX_VALUE else 1,
            shape = RoundedCornerShape(22.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Teal
            ),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    2.dp,
                    borderColor,
                    RoundedCornerShape(22.dp)
                )
        )
    }
}
