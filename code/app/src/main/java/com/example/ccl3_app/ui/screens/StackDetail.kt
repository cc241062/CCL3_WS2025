package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Jua
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.PostItYellow
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.StackDetailViewModel
import com.example.ccl3_app.R

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
                @Suppress("UNCHECKED_CAST")
                return StackDetailViewModel(stackRepository, recipeRepository, stackId) as T
            }
        }
    )

    val stack by viewModel.stack.collectAsState()
    val recipes by viewModel.recipes.collectAsState()

    val headerColor: Color = remember(stack?.color) {
        stack?.color
            ?.let { runCatching { Color(android.graphics.Color.parseColor("#$it")) }.getOrNull() }
            ?: Teal
    }

    val isAllRecipes = stackId == StackDetailViewModel.ALL_RECIPES_STACK_ID


    // ✅ Apply Jua to all Text in this screen
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = Jua)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        val titleEmoji = stack?.emoji ?: "🍳"
                        val titleText = stack?.name ?: "Stack"
                        Text(
                            text = if (isAllRecipes) "All Recipes" else "$titleEmoji  $titleText",
                            fontWeight = FontWeight.Bold,
                            fontFamily = Jua,
                            fontSize = 22.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        if (!isAllRecipes) {

                            IconButton(onClick = { onEditStack(stackId) }) {
                                Image(
                                    painter = painterResource(id = R.drawable.edit),
                                    contentDescription = "Edit Stack",
                                    modifier = Modifier.size(32.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            IconButton(onClick = {
                                stack?.let {
                                    viewModel.deleteStack(it)
                                    onBack()
                                }
                            }) {
                                Image(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "Delete Stack",
                                    modifier = Modifier.size(28.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = headerColor,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )

                )
            },
            floatingActionButton = {
                if (!isAllRecipes) {
                    Box(
                        modifier = Modifier
                                .size(80.dp)
                                .offset(y = (-56).dp)
                                .clickable { onAddRecipe(stackId) },
                            contentAlignment = Alignment.Center

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.add_recipe),
                            contentDescription = "Add Recipe",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
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
                                    containerColor = Orange,
                                    contentColor = Color.White
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
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            top = 16.dp,
                            end = 16.dp,
                            bottom = 120.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recipes) { recipe ->
                            RecipeListItem(
                                title = recipe.title,
                                description = recipe.description,
                                onClick = { onRecipeClick(recipe.id) }
                            )
                        }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PostItYellow.copy(alpha = 0.6f)
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
