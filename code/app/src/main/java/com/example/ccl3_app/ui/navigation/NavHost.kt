package com.example.ccl3_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ccl3_app.ui.screens.HomeScreen
import com.example.ccl3_app.ui.screens.ProfileDetailScreen
import com.example.ccl3_app.ui.screens.ProfileListScreen
import com.example.ccl3_app.ui.screens.QuestScreen
import com.example.ccl3_app.ui.screens.RecipeDetailScreen
import com.example.ccl3_app.ui.screens.RecipeFormScreen
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState

/* ---------------------------------------------------
   Routes (kept in same file)
--------------------------------------------------- */
object Routes {

    const val HOME = "home"
    const val QUEST = "quest"
    const val PROFILES = "profiles"

    const val PROFILE_DETAIL = "profile_detail"
    const val RECIPE_DETAIL = "recipe_detail"
    const val RECIPE_FORM = "recipe_form"

    fun profileDetail(id: Int) = "$PROFILE_DETAIL/$id"
    fun recipeDetail(id: Int) = "$RECIPE_DETAIL/$id"
    fun recipeForm(stackId: Int) = "$RECIPE_FORM/$stackId"
}


/* ---------------------------------------------------
   NavHost
--------------------------------------------------- */
@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        /* ---------------- Home ---------------- */
        composable(Routes.HOME) {
            HomeScreen(
                onOpenProfiles = {
                    navController.navigate(Routes.PROFILES)
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                onAddRecipe = { stackId ->
                    navController.navigate(Routes.recipeForm(stackId))
                }
            )
        }

        composable(Routes.QUEST) {
            QuestScreen()
        }

        /* ---------------- Profiles ---------------- */
        composable(Routes.PROFILES) {
            ProfileListScreen(
                onProfileClick = { profileId ->
                    navController.navigate(Routes.profileDetail(profileId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------------- Profile Detail ---------------- */
        composable(
            route = "${Routes.PROFILE_DETAIL}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments!!.getInt("id")

            ProfileDetailScreen(
                profileId = profileId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------------- Recipe Detail ---------------- */
        composable(
            route = "${Routes.RECIPE_DETAIL}/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments!!.getInt("id")

            RecipeDetailScreen(
                recipeId = recipeId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------------- Recipe Form (Add/Edit) ---------------- */
        composable(
            route = "${Routes.RECIPE_FORM}/{stackId}",
            arguments = listOf(
                navArgument("stackId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val stackId = backStackEntry.arguments!!.getInt("stackId")

            RecipeFormScreen(
                stackId = stackId,
                onDone = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}
    data class BottomNavItem(
            val label: String,
            val route: String,
            val icon: ImageVector
        )

    @Composable
    fun BottomNavBar(navController: androidx.navigation.NavHostController) {

            val items = listOf(
                BottomNavItem("Home", Routes.HOME, Icons.Default.Home),
                BottomNavItem("Quest", Routes.QUEST, Icons.Default.List),
                BottomNavItem("Profile", Routes.PROFILES, Icons.Default.Person)
            )

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(Routes.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
