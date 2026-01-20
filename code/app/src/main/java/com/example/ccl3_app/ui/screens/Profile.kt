package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.R
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.components.RecipeCard
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.PostItYellow
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.ProfileViewModel
import com.example.ccl3_app.ui.viewmodels.ProfileViewModelFactory
import com.example.ccl3_app.ui.viewmodels.StackViewModel
import kotlinx.coroutines.flow.flowOf
import com.example.ccl3_app.ui.theme.Jua



data class StackUi(
    val id: Int,
    val title: String,
    val emoji: String = "🍳"
)

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onStackClick: (Int) -> Unit = {},
    onAddStack: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onEditStack: (Int) -> Unit = {}
) {
    val context = LocalContext.current

    val database = OopsDatabase.getDatabase(context)
    val profileRepo = ProfileRepository(database.ProfileDao())
    val stackRepository = StackRepository(database.StackDao())
    val recipeRepository = RecipeRepository(database.RecipeDao())

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(profileRepo)
    )

    val stackViewModel: StackViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return StackViewModel(stackRepository, recipeRepository) as T
            }
        }
    )

    // ensure profile exists
    LaunchedEffect(Unit) {
        profileRepo.ensureDefaultProfile()
    }

    val profile by profileRepo.observeSingleProfile().collectAsState(initial = null)
    val query by profileViewModel.searchQuery.collectAsState()

    // recipes search
    val recipeResultsFlow = remember(query) {
        if (query.isBlank()) flowOf(emptyList())
        else stackViewModel.searchRecipes(query)   // <- must exist in your ViewModel
    }
    val recipeResults by recipeResultsFlow.collectAsState(initial = emptyList())

    // stacks
    val stacks by stackViewModel.stacks.collectAsState(initial = emptyList())

    val stacksWithAll = remember(stacks) {
        val allStack = com.example.ccl3_app.data.Stack(   // use your actual Stack type
            id = StackViewModel.ALL_RECIPES_STACK_ID,
            name = "All Recipes"
        )
        listOf(allStack) + stacks
    }

    val filteredStacks = remember(query, stacksWithAll) {
        if (query.isBlank()) stacksWithAll
        else stacksWithAll.filter { it.name.contains(query, ignoreCase = true) }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // -------- Main content (what you already had) --------
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // HEADER AREA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .background(Teal.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 10.dp, top = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = profile?.username ?: "Loading...",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE37434), // orange username
                            fontFamily = Jua
                        )

                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color(0xFF0E4851) // settings color
                            )
                        }
                    }

                    // Avatar in center
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 46.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(R.drawable.profile_pic),
                            contentDescription = "Profile picture",
                            modifier = Modifier
                                .size(160.dp)
                                .offset(y = 30.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // CONTENT AREA (search + stacks / recipes)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .offset(y = (-28).dp)
                ) {
                    SearchBar(
                        value = query,
                        onValueChange = profileViewModel::setSearchQuery,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (query.isBlank()) {
                        // NORMAL MODE: show stacks grid
                        Text(
                            text = "All Stacks",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = Jua
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .navigationBarsPadding(),
                            contentPadding = PaddingValues(
                                top = 0.dp,
                                start = 0.dp,
                                end = 0.dp,
                                bottom = 140.dp
                            )
                        ) {
                            items(filteredStacks) { stack ->
                                val recipes by stackViewModel.getRecipesForStack(stack.id)
                                    .collectAsState(initial = emptyList())

                                StackCard(
                                    title = stack.name,
                                    emoji = "🍳",
                                    recipeCount = recipes.size,
                                    onClick = { onStackClick(stack.id) },
                                    onLongClick = { onEditStack(stack.id) }
                                )
                            }
                        }

                    } else {
                        // SEARCH MODE: show recipes grid (direct access)
                        Text(
                            text = "Recipes",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontFamily = Jua
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(recipeResults) { recipe ->
                                RecipeSearchCard(
                                    title = recipe.title,
                                    emoji = "🍽️",
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    }
                }
            }

            // -------- Floating Add Stack button anchored to screen --------
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 74.dp, end = 16.dp)
                    .size(80.dp)
                    .clickable { onAddStack() },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.add_stack),
                    contentDescription = "Add Stack",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = Color(0xFF5C919B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.lupe),
                contentDescription = "Search",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "search...",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Jua
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StackCard(
    title: String,
    emoji: String,
    recipeCount: Int = 0,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = PostItYellow.copy(alpha = 0.55f)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 44.sp,
                    fontFamily = Jua
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = Jua
                )

                Text(
                    text = "$recipeCount",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Orange,
                    fontFamily = Jua,
                    modifier = Modifier
                        .background(Orange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}


@Composable
private fun RecipeSearchCard(
    title: String,
    emoji: String = "🍽️",
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = PostItYellow.copy(alpha = 0.55f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = emoji,
                    fontSize = 44.sp,
                    fontFamily = Jua
                )
            }

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontFamily = Jua,
                maxLines = 1
            )
        }
    }
}


@Composable
private fun RecipeRow(
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        color = Color(0xFFF3F3F3)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                fontFamily = Jua
            )
        }
    }
}
