package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.StackRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Jua
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.theme.PostItYellow
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

    // fixed palette
    val colorOptions = listOf(
        "E37434" to Color(0xFFE37434), // orange
        "4B9DA9" to Color(0xFF4B9DA9), // teal-blue
        "FFCF6E" to Color(0xFFFFCF6E)  // yellow
    )

    // emoji palette
    val emojiOptions = listOf("🍳", "🍽️", "🥗", "🍰", "🥐", "🍣", "🍜", "🧁", "🍕", "🌮", "🥪", "🥞")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // HEADER
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
                text = if (stackId == null) "Create Stack" else "Edit Stack",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // NAME
            StackField(
                label = "Stack name",
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                placeholder = "e.g. Breakfast, Lunch, Desserts",
                containerColor = Teal.copy(alpha = 0.16f),
                borderColor = Teal.copy(alpha = 0.9f),
                multiLine = false
            )

            // COLOR PICKER
            Text(
                text = "Color",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                colorOptions.forEach { (hex, color) ->
                    val isSelected = hex == viewModel.color
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 40.dp else 32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected)
                                    Color.Black.copy(alpha = 0.4f)
                                else
                                    Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { viewModel.color = hex }
                    )
                }
            }

            // EMOJI PICKER
            Text(
                text = "Emoji",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Jua
            )

            Spacer(modifier = Modifier.height(6.dp))

            val emojiScroll = rememberScrollState()

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(emojiScroll)
            ) {
                emojiOptions.forEach { emoji ->
                    val isSelected = emoji == viewModel.emoji
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = if (isSelected) 4.dp else 0.dp,
                        shadowElevation = if (isSelected) 4.dp else 0.dp,
                        color = if (isSelected)
                            PostItYellow.copy(alpha = 0.9f)
                        else
                            Color.Transparent,
                        modifier = Modifier
                            .padding(vertical = 2.dp)
                            .clickable { viewModel.emoji = emoji }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = emoji,
                                fontSize = 28.sp
                            )
                        }
                    }
                }
            }

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
    val darkTeal = Color(0xFF0E4851)

    Column {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Jua,
            color = darkTeal
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = LocalTextStyle.current.copy(
                fontFamily = Jua,
                fontSize = 16.sp,
                color = darkTeal
            ),
            placeholder = {
                Text(
                    text = placeholder,
                    fontFamily = Jua,
                    fontSize = 16.sp,
                    color = darkTeal.copy(alpha = 0.5f)
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
                cursorColor = darkTeal,
                selectionColors = TextSelectionColors(
                    handleColor = darkTeal,
                    backgroundColor = darkTeal.copy(alpha = 0.3f)
                )
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
