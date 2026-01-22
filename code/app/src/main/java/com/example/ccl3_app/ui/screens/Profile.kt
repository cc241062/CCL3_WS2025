package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.R
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Jua
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.PostItYellow
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.ProfileViewModel
import com.example.ccl3_app.ui.viewmodels.ProfileViewModelFactory
import com.example.ccl3_app.ui.viewmodels.StackViewModel
import kotlinx.coroutines.flow.flowOf
import androidx.compose.material3.LocalTextStyle
import android.graphics.Color as AndroidColor

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

    val recipeResultsFlow = remember(query) {
        if (query.isBlank()) flowOf(emptyList<Recipe>())
        else stackViewModel.searchRecipes(query)
    }
    val recipeResults by recipeResultsFlow.collectAsState(initial = emptyList())

    val stacks by stackViewModel.stacks.collectAsState(initial = emptyList())

    val stacksWithAll = remember(stacks) {
        val allStack = com.example.ccl3_app.data.Stack(
            id = StackViewModel.ALL_RECIPES_STACK_ID,
            name = "All Recipes",
            color = "F6F3C2",
            emoji = "🍳"
        )
        listOf(allStack) + stacks
    }

    val filteredStacks = remember(query, stacksWithAll) {
        if (query.isBlank()) stacksWithAll
        else stacksWithAll.filter { it.name.contains(query, ignoreCase = true) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Main layout: top 1/3, bottom 2/3
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopProfileArea(
                profileName = profile?.username ?: "Loading...",
                query = query,
                onQueryChange = profileViewModel::setSearchQuery,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)   // 1/3
            )

            BottomContentArea(
                query = query,
                filteredStacks = filteredStacks,
                stackViewModel = stackViewModel,
                recipeResults = recipeResults,
                onStackClick = onStackClick,
                onEditStack = onEditStack,
                onRecipeClick = onRecipeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)   // 2/3
            )
        }

        // Floating Add Stack button (unchanged)
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

@Composable
private fun TopProfileArea(
    profileName: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {

        // Light teal background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)   // goes down behind about half of search bar
                .background(Teal.copy(alpha = 0.15f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            // Username + settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = profileName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFE37434),
                    fontFamily = Jua,
                    maxLines = 1
                )

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF0E4851)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Avatar + SearchBar stacked so bar hides neck
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                // Avatar behind
                Image(
                    painter = painterResource(R.drawable.profile_pic),
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(110.dp)
                        .offset(y = (-15).dp),
                    contentScale = ContentScale.Fit
                )

                // Search bar in front, overlapping lower part of avatar
                SearchBar(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp)
                        .offset(y = (-6).dp)
                )
            }
        }
    }
}

@Composable
private fun BottomContentArea(
    query: String,
    filteredStacks: List<com.example.ccl3_app.data.Stack>,
    stackViewModel: StackViewModel,
    recipeResults: List<Recipe>,
    onStackClick: (Int) -> Unit,
    onEditStack: (Int) -> Unit,
    onRecipeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        if (query.isBlank()) {
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
                    val recipes by stackViewModel
                        .getRecipesForStack(stack.id)
                        .collectAsState(initial = emptyList())

                    val cardColor = remember(stack.color, stack.id) {
                        if (stack.id == StackViewModel.ALL_RECIPES_STACK_ID) {
                            Color(0xFFF6F3C2)  // fixed color for "All Recipes"
                        } else {
                            runCatching {
                                Color(AndroidColor.parseColor("#${stack.color}"))
                            }.getOrDefault(PostItYellow.copy(alpha = 0.55f))
                        }
                    }

                    StackCard(
                        title = stack.name,
                        emoji = stack.emoji,
                        recipeCount = recipes.size,
                        backgroundColor = cardColor,
                        onClick = { onStackClick(stack.id) },
                        onLongClick = { onEditStack(stack.id) }
                    )
                }
            }
        } else {
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

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 3.dp,
        shadowElevation = 5.dp,
        color = Color(0xFF5C919B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.lupe),
                contentDescription = "Search",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(16.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        "search...",
                        color = Color.White.copy(alpha = 0.8f),
                        fontFamily = Jua,
                        fontSize = 13.sp
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
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = Jua,
                    fontSize = 13.sp
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
    backgroundColor: Color,
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
        color = backgroundColor
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
                    fontFamily = FontFamily.Default
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
