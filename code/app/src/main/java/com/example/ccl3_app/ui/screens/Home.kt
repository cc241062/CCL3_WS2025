package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.R
import com.example.ccl3_app.data.Recipe
import com.example.ccl3_app.ui.theme.*
import com.example.ccl3_app.ui.viewmodels.HomeViewModel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

val JuaFont = FontFamily(Font(R.font.jua_regular, FontWeight.Normal))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onOpenProfiles: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onAddRecipe: (Int) -> Unit = {},
    onNavigateToQuests: () -> Unit = {}
) {
    val featuredRecipes by viewModel.featuredRecipes.collectAsState()
    val stacks by viewModel.stacks.collectAsState()
    val selectedStackId by viewModel.selectedStackId.collectAsState()
    val currentRecipeIndex by viewModel.currentRecipeIndex.collectAsState()

    val currentRecipe: Recipe? = remember(currentRecipeIndex, selectedStackId, featuredRecipes) {
        viewModel.getCurrentRecipe()
    }

    var stackMenuExpanded by remember { mutableStateOf(false) }

    val selectedStackName = when (selectedStackId) {
        null -> "All recipes"
        else -> stacks.firstOrNull { it.id == selectedStackId }?.name ?: "All recipes"
    }

    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp

    // 🔹 FAB size is responsive
    val fabSize = when {
        screenWidth < 340 -> 64.dp
        screenWidth < 400 -> 72.dp
        else -> 80.dp
    }

    // 🔹 distance above bottom (so it clears bottom nav nicely)
    val fabBottomPadding = when {
        configuration.screenHeightDp < 650 -> 10.dp
        configuration.screenHeightDp < 800 -> 12.dp
        else -> 14.dp
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0), // outer Scaffold already handles system bars

        floatingActionButton = {
            Box(
                modifier = Modifier
                    .padding(
                        bottom = fabBottomPadding,
                        end = 8.dp
                    )
                    .size(fabSize)
                    .clickable {
                        onAddRecipe(selectedStackId ?: 1)
                    },
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                WelcomeQuestCard(onClick = onNavigateToQuests)

                Spacer(Modifier.height(18.dp))

                Divider(color = Color(0xFFE0E0E0), thickness = 2.dp)

                Spacer(Modifier.height(14.dp))

                // ------- "Find your match:" header + dropdown -------
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Find your match:",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = JuaFont,
                            color = Color(0xFFE37434)
                        )

                        Box {
                            TextButton(
                                onClick = { stackMenuExpanded = true },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = selectedStackName,
                                    fontSize = 16.sp,
                                    fontFamily = JuaFont,
                                    color = Color(0xFF555555)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color(0xFF555555),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = stackMenuExpanded,
                                onDismissRequest = { stackMenuExpanded = false }
                            ) {
                                DropdownMenu(
                                    expanded = stackMenuExpanded,
                                    onDismissRequest = { stackMenuExpanded = false },
                                    modifier = Modifier
                                        .background(Color(0xFFFFE4C7))
                                ) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = "All recipes",
                                                fontFamily = JuaFont,
                                                color = Color(0xFFAA5423)
                                            )
                                        },
                                        onClick = {
                                            viewModel.selectStack(null)
                                            stackMenuExpanded = false
                                        },
                                        modifier = Modifier.background(Color.Transparent)
                                    )

                                    Divider(color = Color(0xFFE0A36E))

                                    stacks.forEach { stack ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = stack.name,
                                                    fontFamily = JuaFont,
                                                    color = Color(0xFFAA5423)
                                                )
                                            },
                                            onClick = {
                                                viewModel.selectStack(stack.id)
                                                stackMenuExpanded = false
                                            },
                                            modifier = Modifier.background(Color.Transparent)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- Recipe Card Stack ---
                if (currentRecipe != null) {
                    val recipesInStack = featuredRecipes.filter { it.stackId == selectedStackId }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.8f)
                    ) {
                        RecipeCardStack(
                            recipe = currentRecipe,
                            recipesCount = recipesInStack.size,
                            currentIndex = currentRecipeIndex,
                            onRecipeClick = { onRecipeClick(currentRecipe.id) },
                            cardHeight = screenHeight * 0.8f,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(0.8f)
                        )

                        RecipeStackArrows(
                            modifier = Modifier.align(Alignment.Center),
                            onPreviousClick = {
                                viewModel.previousRecipe()
                            },
                            onNextClick = {
                                viewModel.nextRecipe()
                            }
                        )
                    }
                } else {
                    EmptyRecipeCard(onAddRecipe = onAddRecipe)
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}


// ------------------ other composables unchanged --------------------

@Composable
fun WelcomeQuestCard(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        // BACK CARD
        Card(
            modifier = Modifier
                .matchParentSize()
                .offset(y = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1C393D)
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {}

        // FRONT CARD
        Card(
            onClick = onClick,
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Teal
            ),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hi, hungry.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JuaFont,
                        color = Color.White
                    )
                    Text(
                        text = "Welcome back!",
                        fontSize = 18.sp,
                        fontFamily = JuaFont,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "try the newest quest.",
                        fontSize = 14.sp,
                        fontFamily = JuaFont,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color(0xFF1C393D))
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(id = R.drawable.quest_white),
                    contentDescription = "Quest",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
fun RecipeCardStack(
    recipe: Recipe,
    recipesCount: Int = 1,
    currentIndex: Int = 0,
    onRecipeClick: () -> Unit = {},
    cardHeight: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(cardHeight * 0.88f)
                    .offset(y = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0E4851)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {}

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.96f)
                    .height(cardHeight * 0.94f)
                    .offset(y = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF4B9DA9)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {}

            Card(
                onClick = onRecipeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFC8F4EC)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight * 0.45f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🍳", fontSize = 80.sp)
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = recipe.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JuaFont,
                        color = Color(0xFF0E4851)
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "5min",
                            fontSize = 16.sp,
                            fontFamily = JuaFont,
                            color = Color.Gray
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Description:",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JuaFont,
                        color = Color(0xFF0E4851)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        recipe.description,
                        fontSize = 14.sp,
                        fontFamily = JuaFont,
                        color = Color.Black.copy(alpha = 0.7f),
                        lineHeight = 20.sp,
                        maxLines = 3
                    )

                    Spacer(Modifier.height(8.dp))

                    if (recipesCount > 1) {
                        Text(
                            "${currentIndex + 1} / $recipesCount",
                            fontSize = 12.sp,
                            fontFamily = JuaFont,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeStackArrows(
    modifier: Modifier = Modifier,
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.arrow_right),
                contentDescription = "Previous",
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(32.dp)
                    .clickable { onPreviousClick() }
            )

            Image(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Next",
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(32.dp)
                    .clickable { onNextClick() }
            )
        }
    }
}

@Composable
fun EmptyRecipeCard(
    onAddRecipe: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightTeal
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "📝",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No Recipes Yet",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = JuaFont,
                color = Teal
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Start your cooking journey by creating your first recipe!",
                fontSize = 16.sp,
                fontFamily = JuaFont,
                color = Color.Black.copy(alpha = 0.7f),
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAddRecipe(1) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Recipe",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Create Your First Recipe",
                    fontSize = 16.sp,
                    fontFamily = JuaFont,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
