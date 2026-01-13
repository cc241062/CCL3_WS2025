package com.example.ccl3_app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ccl3_app.ui.components.OopsCard
import com.example.ccl3_app.ui.components.RecipeCard
import com.example.ccl3_app.ui.theme.DarkTeal
import com.example.ccl3_app.ui.theme.Orange


@Composable
fun HomeScreen(
    onOpenProfiles: () -> Unit = {},
    onRecipeClick: (Int) -> Unit = {},
    onAddRecipe: (Int) -> Unit = {}
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Oops! I can cook",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkTeal
        )

        Spacer(modifier = Modifier.height(12.dp))

        OopsCard {
            Text("Hii, welcome back 👋")
            Spacer(Modifier.height(4.dp))
            Text("Are you hungry?")
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Let’s cook!",
                color = Orange,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OopsCard {
            Text(
                text = "New quests available",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Have a look!")
        }

        RecipeCard(
            backGroundColor = Orange
        ) {
            Text("Recipe")
        }

        Text("Home Screen")
        Button(onClick = onOpenProfiles) {
            Text("Open Profiles")
        }
        Button(onClick = { onRecipeClick(1) }) {
            Text("Go to Recipe Detail (id=1)")
        }
        Button(onClick = { onAddRecipe(1) }) {
            Text("Add Recipe (stackId=1)")
        }

    }
}
