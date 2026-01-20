package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ccl3_app.R
import com.example.ccl3_app.data.Profile
import com.example.ccl3_app.data.ProfileRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.theme.Teal
import com.example.ccl3_app.ui.viewmodels.ProfileDetailViewModel
import com.example.ccl3_app.ui.viewmodels.ProfileDetailViewModelFactory
import com.example.ccl3_app.ui.theme.Jua



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
    var showCannotDeleteDialog by remember { mutableStateOf(false) }

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
        // Top header bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF4B9DA9))  // blue-teal header
                .padding(vertical = 16.dp),
        ) {

            // Back arrow in top-left
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White   // icon white
                )
            }

            // Centered title
            Text(
                text = "Profil",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,   // text white
                fontFamily = Jua,
                modifier = Modifier.align(Alignment.Center)
            )
        }



        if (showCannotDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showCannotDeleteDialog = false },
                title = {
                    Text(
                        "Not possible",
                        fontFamily = Jua,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "You can not delete the test user.",
                        fontFamily = Jua
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showCannotDeleteDialog = false }) {
                        Text("OK", fontFamily = Jua)
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // (Profile picture removed on purpose)

            Spacer(modifier = Modifier.height(10.dp))

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

            // Buttons row at bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showCannotDeleteDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0x33FF0000),
                        contentColor = Color(0xFF730000)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFF730000)
                    ),
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Delete account",
                        fontFamily = Jua,
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
                        containerColor = Color(0xFFE37434),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        text = "Save changes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Jua
                    )
                }

            }

            Spacer(modifier = Modifier.height(18.dp))

            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Back", fontFamily = Jua)
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
            color = Color.Black,
            fontFamily = Jua
        )

        Spacer(modifier = Modifier.height(6.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = LocalTextStyle.current.copy(fontFamily = Jua),
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
