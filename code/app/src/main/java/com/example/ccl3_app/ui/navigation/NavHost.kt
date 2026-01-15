package com.example.ccl3_app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ccl3_app.data.RecipeRepository
import com.example.ccl3_app.data.QuestRepository
import com.example.ccl3_app.database.OopsDatabase
import com.example.ccl3_app.ui.screens.HomeScreen
import com.example.ccl3_app.ui.screens.ProfileDetailScreen
import com.example.ccl3_app.ui.screens.ProfileScreen
import com.example.ccl3_app.ui.screens.QuestScreen
import com.example.ccl3_app.ui.screens.RecipeDetailScreen
import com.example.ccl3_app.ui.screens.RecipeFormScreen
import com.example.ccl3_app.ui.viewmodels.HomeViewModel
import com.example.ccl3_app.ui.viewmodels.HomeViewModelFactory
import com.example.ccl3_app.ui.viewmodels.QuestViewModel
import com.example.ccl3_app.ui.viewmodels.QuestViewModelFactory
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
    const val PROFILE = "profile"

    const val PROFILE_DETAIL = "profile_detail"
    const val RECIPE_DETAIL = "recipe_detail"
    const val RECIPE_FORM = "recipe_form"

    fun profileDetail(id: Int) = "$PROFILE_DETAIL/$id"
    fun recipeDetail(id: Int) = "$RECIPE_DETAIL/$id"
    fun recipeForm(stackId: Int = 0, recipeId: Int? = null) =
        if (recipeId != null) "$RECIPE_FORM/$stackId/$recipeId"
        else "$RECIPE_FORM/$stackId/-1"
}


/* ---------------------------------------------------
   NavHost
--------------------------------------------------- */
@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = OopsDatabase.getDatabase(context)
    val recipeRepository = RecipeRepository(database.RecipeDao())
    val questRepository = QuestRepository(database.QuestDao())

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        /* ---------------- Home ---------------- */
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(recipeRepository)
            )

            HomeScreen(
                viewModel = homeViewModel,
                onOpenProfiles = {
                    navController.navigate(Routes.PROFILE)
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                onAddRecipe = { stackId ->
                    navController.navigate(Routes.recipeForm(stackId))
                },
                onNavigateToQuests = {
                    navController.navigate(Routes.QUEST)
                }
            )
        }

        /* ---------------- Quest ---------------- */
        composable(Routes.QUEST) {
            val questViewModel: QuestViewModel = viewModel(
                factory = QuestViewModelFactory(questRepository)
            )
            QuestScreen(viewModel = questViewModel)
        }

        /* ---------------- Profiles ---------------- */
        composable(Routes.PROFILE) {
            ProfileScreen(
                onSettingsClick = {
                    navController.navigate(Routes.PROFILE_DETAIL)
                },
                onStackClick = { /* TODO */ },
                onAddStack = { /* TODO */ }
            )

        }

        /* ---------------- Profile Detail ---------------- */
        composable(Routes.PROFILE_DETAIL) {
            ProfileDetailScreen(
                onBack = { navController.popBackStack() }
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
                },
                onEdit = { id ->
                    navController.navigate(Routes.recipeForm(/*stackId = id,*/ recipeId = id))
                }
            )
        }

        /* ---------------- Recipe Form (Add/Edit) ---------------- */
        composable(
            route = "${Routes.RECIPE_FORM}/{stackId}/{recipeId}",  // ← Changed this
            arguments = listOf(
                navArgument("stackId") { type = NavType.IntType },
                navArgument("recipeId") {
                    type = NavType.IntType
                    //nullable = true
                    defaultValue = -1  // ← Use -1 instead of null
                }
            )
        ) { backStackEntry ->
            val stackId = backStackEntry.arguments!!.getInt("stackId")
            val recipeId = backStackEntry.arguments?.getInt("recipeId")

            RecipeFormScreen(
                stackId = stackId,
                recipeId = if (recipeId == -1) null else recipeId,  // ← Check for -1
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
        BottomNavItem("Profile", Routes.PROFILE, Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination and save state
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when navigating back
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}