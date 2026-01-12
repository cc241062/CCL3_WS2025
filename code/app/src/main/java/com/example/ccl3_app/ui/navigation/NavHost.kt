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
import com.example.ccl3_app.ui.screens.RecipeDetailScreen
import com.example.ccl3_app.ui.screens.RecipeFormScreen

/* ---------------------------------------------------
   Routes (kept in same file)
--------------------------------------------------- */
object Routes {

    const val HOME = "home"
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
fun AppNavHost(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

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
