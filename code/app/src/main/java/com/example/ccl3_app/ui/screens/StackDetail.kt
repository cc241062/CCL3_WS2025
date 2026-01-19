package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.LightTeal
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.PostItYellow
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.StackDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackDetailScreen(
    stackId: Int,
    onBack: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onAddRecipe: (Int) -> Unit = {},
    onEditStack: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val database = OopsDatabase.getDatabase(context)
    val stackRepository = StackRepository(database.StackDao())
    val recipeRepository = RecipeRepository(database.RecipeDao())

    val viewModel: StackDetailViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return StackDetailViewModel(stackRepository, recipeRepository, stackId) as T
            }
        }
    )

    val stack by viewModel.stack.collectAsState()
    val recipes by viewModel.recipes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stack?.name ?: "Stack",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },

                actions = {  // ← Add this actions block
                    IconButton(onClick = { onEditStack(stackId) }) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Stack",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Teal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddRecipe(stackId) },
                containerColor = Orange,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Recipe")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            if (recipes.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "📝",
                            fontSize = 72.sp
                        )
                        Text(
                            text = "No recipes in this stack yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Button(
                            onClick = { onAddRecipe(stackId) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Orange
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add First Recipe")
                        }
                    }
                }
            } else {
                // Recipe list
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(recipes) { index, recipe ->
                        RecipeListItem(
                            title = recipe.title,
                            description = recipe.description,
                            index = index,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeListItem(
    title: String,
    description: String,
    index: Int = 0,
    onClick: () -> Unit
) {
    val cardColors = listOf(
        PostItYellow.copy(alpha = 0.6f),
        LightTeal,
        Orange.copy(alpha = 0.2f),
        Teal.copy(alpha = 0.3f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColors[index % cardColors.size]
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Black.copy(alpha = 0.7f),
                maxLines = 2
            )
        }
    }
}