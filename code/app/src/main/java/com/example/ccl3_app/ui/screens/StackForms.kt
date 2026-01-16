package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.viewmodels.StackFormsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StackFormScreen(
    stackId: Int?,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val database = OopsDatabase.getDatabase(context)
    val stackRepository = StackRepository(database.StackDao())

    val viewModel: StackFormsViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return StackFormsViewModel(stackRepository) as T
            }
        }
    )

    LaunchedEffect(stackId) {
        stackId?.let { viewModel.loadStack(it) }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (stackId == null) "Create Stack" else "Edit Stack",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Stack Name") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g., Breakfast, Italian, Desserts") }
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                placeholder = { Text("What kind of recipes will be in this stack?") }
            )

            OutlinedTextField(
                value = viewModel.emoji,
                onValueChange = { viewModel.emoji = it },
                label = { Text("Emoji") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("🍳") }
            )

            Button(
                onClick = {
                    viewModel.saveStack()
                    onDone()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange)
            ) {
                Text("Save Stack")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}