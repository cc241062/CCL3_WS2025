package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileListScreen(
    onProfileClick: (Int) -> Unit = {},
    onBack: () -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Profile List Screen")
        Button(onClick = { onProfileClick(1) }) {
            Text("Open Profile Detail (id=1)")
        }
        Button(onClick = onBack) {
            Text("Back")
        }
    }
}
