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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

val JuaFont = FontFamily(
    Font(R.font.jua_regular, FontWeight.Normal)
)

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
    val currentRecipeIndex by viewModel.currentRecipeIndex.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val stackOptions = listOf("For dinner", "For breakfast", "For lunch")
    var stackMenuExpanded by remember { mutableStateOf(false) }
    var selectedStack by remember { mutableStateOf(stackOptions.first()) }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // -------- Scrollable content --------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Welcome Card
            WelcomeQuestCard(onClick = onNavigateToQuests)

            Divider(
                color = Color(0xFFE0E0E0),
                thickness = 2.dp
            )

            // "Find you match:" header + dropdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Find you match:",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JuaFont,
                        color = Orange
                    )

                    Box(
                        modifier = Modifier.offset(y = (-4).dp)
                    ) {
                        TextButton(
                            onClick = { stackMenuExpanded = true },
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = selectedStack,
                                fontSize = 18.sp,
                                fontFamily = JuaFont,
                                color = Color.LightGray
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = stackMenuExpanded,
                            onDismissRequest = { stackMenuExpanded = false }
                        ) {
                            stackOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = option,
                                            fontFamily = JuaFont
                                        )
                                    },
                                    onClick = {
                                        selectedStack = option
                                        stackMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Recipe Card Stack
            val currentRecipe = featuredRecipes.getOrNull(currentRecipeIndex)
            if (currentRecipe != null) {
                RecipeCardStack(
                    recipe = currentRecipe,
                    onRecipeClick = { onRecipeClick(currentRecipe.id) },
                    onPreviousClick = { viewModel.previousRecipe() },
                    onNextClick = { viewModel.nextRecipe() }
                )
            } else {
                EmptyRecipeCard(onAddRecipe = onAddRecipe)
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // -------- Floating Add Recipe button anchored to screen --------
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 88.dp, end = 16.dp)
                .size(96.dp)
                .clickable { onAddRecipe(1) },
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


@Composable
fun WelcomeQuestCard(onClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
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
                    .padding(start = 24.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ----- Text -----
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Hi, hungry.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = JuaFont,
                        color = Color.White
                    )
                    Text(
                        text = "Welcome back!",
                        fontSize = 20.sp,
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

                // ----- FULL HEIGHT vertical divider -----
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Color(0xFF1C393D))
                )

                Spacer(modifier = Modifier.width(16.dp))

                // ----- Icon -----
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
    onRecipeClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onAddRecipe: (Int) -> Unit = {}          // you can keep this if you still want to use it later
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        // Back cards
        Card(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(480.dp)
                .offset(x = 12.dp, y = 12.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Orange.copy(alpha = 0.3f)
            )
        ) {}

        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .height(480.dp)
                .offset(x = 6.dp, y = 6.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFC8F4EC)
            )
        ) {}

        // Main card
        Card(
            onClick = onRecipeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightTeal
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Recipe Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: actual image
                    Text(
                        text = "🍳",
                        fontSize = 80.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Recipe Title
                Text(
                    text = recipe.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JuaFont,
                    color = Teal
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Duration with clock icon
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clock),
                        contentDescription = "Duration",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "5min", // make dynamic later
                        fontSize = 16.sp,
                        fontFamily = JuaFont,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description header
                Text(
                    text = "Description:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = JuaFont,
                    color = Teal
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Description text
                Text(
                    text = recipe.description,
                    fontSize = 14.sp,
                    fontFamily = JuaFont,
                    color = Color.Black.copy(alpha = 0.7f),
                    lineHeight = 20.sp,
                    maxLines = 3
                )
            }
        }

        // Navigation arrows
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousClick,
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = (-16).dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(
                onClick = onNextClick,
                modifier = Modifier
                    .size(48.dp)
                    .offset(x = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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