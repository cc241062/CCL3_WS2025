package com.example.ccl3_app.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ccl3_app.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.ccl3_app.data.StackRepository


import com.example.ccl3_app.ui.screens.StackDetailScreen
import com.example.ccl3_app.ui.screens.StackFormScreen

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

    const val STACK_DETAIL = "stack_detail"
    const val STACK_FORM = "stack_form"


    fun profileDetail(id: Int) = "$PROFILE_DETAIL/$id"
    fun recipeDetail(id: Int) = "$RECIPE_DETAIL/$id"
    fun recipeForm(stackId: Int = 0, recipeId: Int? = null) =
        if (recipeId != null) "$RECIPE_FORM/$stackId/$recipeId"
        else "$RECIPE_FORM/$stackId/-1"
    fun stackDetail(stackId: Int) = "$STACK_DETAIL/$stackId"

    fun stackForm(stackId: Int? = null) =
        if (stackId != null) "$STACK_FORM/$stackId"
        else "$STACK_FORM/-1"
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
    val stackRepository = StackRepository(database.StackDao())

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        /* ---------------- Home ---------------- */
        composable(Routes.HOME) {
            val homeViewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(recipeRepository,stackRepository)
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
                onStackClick = { stackId ->  // ← Handle navigation here
                    navController.navigate(Routes.stackDetail(stackId))
                },
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

        /* ---------------- Stack Detail ---------------- */
        composable(
            route = "${Routes.STACK_DETAIL}/{stackId}",
            arguments = listOf(
                navArgument("stackId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val stackId = backStackEntry.arguments!!.getInt("stackId")

            StackDetailScreen(
                stackId = stackId,
                onBack = {
                    navController.popBackStack()
                },
                onRecipeClick = { recipeId ->
                    navController.navigate(Routes.recipeDetail(recipeId))
                },
                onAddRecipe = { stackId ->
                    navController.navigate(Routes.recipeForm(stackId = stackId))
                },
                onEditStack = { stackId ->  // ← Add this
                    navController.navigate(Routes.stackForm(stackId))
                }
            )
        }

        /* ---------------- Stack Form (Add/Edit) ---------------- */
        composable(
            route = "${Routes.STACK_FORM}/{stackId}",
            arguments = listOf(
                navArgument("stackId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val stackId = backStackEntry.arguments?.getInt("stackId")

            StackFormScreen(
                stackId = if (stackId == -1) null else stackId,
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
    val icon: Int
)

@Composable
fun BottomNavBar(navController: NavHostController) {

    val items = listOf(
        BottomNavItem("Home", Routes.HOME, R.drawable.home),
        BottomNavItem("Quest", Routes.QUEST, R.drawable.quest),
        BottomNavItem("Profile", Routes.PROFILE, R.drawable.profile)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {

        // Thin grey line on top
        HorizontalDivider(
            thickness = 1.dp,
            color = Color.LightGray
        )

        NavigationBar(
            containerColor = Color.White,
            tonalElevation = 0.dp,
            modifier = Modifier.height(64.dp)
        ) {
            items.forEach { item ->

                val selected = currentRoute == item.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    label = null,
                    alwaysShowLabel = false,

                    // 🔹 Our custom highlight + icon
                    icon = {
                        val boxModifier =
                            if (selected) {
                                Modifier
                                    .padding(top = 4.dp)
                                    .background(
                                        color = Color(0xFFF3F3F3), // very light grey
                                        shape = RoundedCornerShape(18.dp)
                                    )
                                    .padding(10.dp)
                            } else {
                                Modifier.padding(top = 4.dp)
                            }

                        Box(modifier = boxModifier) {
                            Image(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                modifier = Modifier.size(36.dp) // 🔸 larger icon
                            )
                        }
                    },

                    // 🔹 Turn OFF Material's default indicator & tint
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = Color.Unspecified,
                        unselectedIconColor = Color.Unspecified,
                        selectedTextColor = Color.Unspecified,
                        unselectedTextColor = Color.Unspecified
                    )
                )
            }
        }
    }
}