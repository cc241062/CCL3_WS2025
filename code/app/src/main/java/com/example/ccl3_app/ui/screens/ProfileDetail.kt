package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.data.Profile
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.ProfileDetailViewModel
import com.example.ccl3_app.ui.viewmodels.ProfileDetailViewModelFactory

@Composable
fun ProfileDetailScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = OopsDatabase.getDatabase(context)
    val repo = ProfileRepository(database.ProfileDao())

    val vm: ProfileDetailViewModel = viewModel(
        factory = ProfileDetailViewModelFactory(repo)
    )

    LaunchedEffect(Unit) { vm.loadProfile() }

    val profile by vm.profile.collectAsState()

    // Local editable states (filled when profile loads)
    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            username = it.username
            email = it.email
            password = it.password
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top header bar like mock (mint background + centered title)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Teal.copy(alpha = 0.15f))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Profile",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Avatar circle
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(170.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color.Black, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🍴", fontSize = 72.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Change profile pic",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { /* TODO: open image picker */ },
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileField(
                label = "Name",
                value = name,
                onValueChange = { name = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileField(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileField(
                label = "Email",
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileField(
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Buttons row at bottom like mock
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        profile?.let { vm.deleteProfile(it) }
                        onBack()
                    },
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFB3261E) // red-ish
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Delete account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        val current = profile ?: return@Button
                        vm.updateProfile(
                            Profile(
                                id = current.id,
                                name = name,
                                username = username,
                                email = email,
                                password = password,
                                profileImage = current.profileImage
                            )
                        )
                        onBack()
                    },
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Teal.copy(alpha = 0.35f),
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Save changes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Optional back button (if you still want it visible)
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            shape = RoundedCornerShape(22.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Teal.copy(alpha = 0.15f),
                unfocusedContainerColor = Teal.copy(alpha = 0.15f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .border(2.dp, Teal.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
        )
    }
}
