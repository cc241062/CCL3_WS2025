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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Jua
import com.example.ccl3_app.ui.theme.Orange
import com.example.ccl3_app.ui.theme.Teal
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
                @Suppress("UNCHECKED_CAST")
                return StackFormsViewModel(stackRepository) as T
            }
        }
    )

    LaunchedEffect(stackId) {
        stackId?.let { viewModel.loadStack(it) }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // ----- HEADER (same vibe as Recipe form) -----
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B9DA9)) // teal header
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
                text = if (stackId == null) "Create Stack" else "Edit Stack",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ----- CONTENT -----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            StackField(
                label = "Stack name",
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                placeholder = "e.g. Breakfast, Lunch, Desserts",
                containerColor = Teal.copy(alpha = 0.16f),
                borderColor = Teal.copy(alpha = 0.9f),
                multiLine = false
            )

            StackField(
                label = "Description (optional)",
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                placeholder = "What kind of recipes will be in this stack?",
                containerColor = Teal.copy(alpha = 0.12f),
                borderColor = Teal.copy(alpha = 0.75f),
                multiLine = true
            )

            StackField(
                label = "Emoji",
                value = viewModel.emoji,
                onValueChange = { viewModel.emoji = it },
                placeholder = "🍳",
                containerColor = Teal.copy(alpha = 0.10f),
                borderColor = Teal.copy(alpha = 0.7f),
                multiLine = false
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.saveStack()
                    onDone()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE37434),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save Stack",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = Jua
                )
            }

            Spacer(modifier = Modifier.height(18.dp))


        }
    }
}

@Composable
private fun StackField(
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
            fontFamily = Jua,
            color = Color.Black
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
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(22.dp)
                )
        )
    }
}
