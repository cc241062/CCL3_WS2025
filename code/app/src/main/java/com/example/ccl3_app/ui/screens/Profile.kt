package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.data.Stack
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.theme.PostItYellow
import com.example.ccl3_app.ui.viewmodels.ProfileViewModel
import com.example.ccl3_app.ui.viewmodels.ProfileViewModelFactory
import com.example.ccl3_app.ui.viewmodels.StackViewModel

data class StackUi(
    val id: Int,
    val title: String,
    val emoji: String = "🍳"
)

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onStackClick: (Int) -> Unit = {},
    onAddStack: () -> Unit = {}
) {

    val context = LocalContext.current

    val database = OopsDatabase.getDatabase(context)
    val repo = ProfileRepository(database.ProfileDao())

    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(repo)
    )

    val stackRepository = StackRepository(database.StackDao())

    val stackViewModel: StackViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return StackViewModel(stackRepository) as T
            }
        }
    )


    // set which profile to observe
    LaunchedEffect(Unit) {
        repo.ensureDefaultProfile()
    }

    val profile by repo.observeSingleProfile().collectAsState(initial = null)

    val query by profileViewModel.searchQuery.collectAsState()

    // For now, static stacks list (replace with your real stack data later)
    val stacks by stackViewModel.stacks.collectAsState(initial = emptyList())


    val filteredStacks = remember(query, stacks) {
        if (query.isBlank()) stacks
        else stacks.filter { it.name.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                    .padding(start = 16.dp, end = 10.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = profile?.username ?: "Loading...",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black
                )

                IconButton(onClick = onSettingsClick ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.Black
                    )
                }
            }

            // Avatar / mascot in center
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 46.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Replace with Image later (profile.profileImage)
                    Text(text = "🍴", fontSize = 72.sp)
                }
            }
        }

        // CONTENT AREA (search + stacks)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "All Stacks",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                FloatingActionButton(
                    onClick = { stackViewModel.addStack() },
                    containerColor = Orange,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add stack",
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(stacks) { stack ->
                    StackCard(
                        title = stack.name,
                        emoji = "🍳", // placeholder for now
                        onClick = { onStackClick(stack.id) }
                    )
                }

                item {
                    AddStackCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onAddStack
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
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        color = Color(0xFF5C919B)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.width(10.dp))

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text("search...", color = Color.White.copy(alpha = 0.7f))
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
                Text(text = emoji, fontSize = 44.sp)
            }

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun AddStackCard(
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
        color = Color(0xFFE0E0E0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Add a new stack",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.75f)
            )
        }
    }
}
