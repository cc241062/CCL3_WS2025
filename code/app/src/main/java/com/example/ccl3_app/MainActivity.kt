package com.example.ccl3_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.ccl3_app.ui.navigation.AppNavHost
import com.example.ccl3_app.ui.navigation.BottomNavBar
import com.example.ccl3_app.ui.screens.SplashScreen
import com.example.ccl3_app.ui.theme.CCL3_AppTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CCL3_AppTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }


                if (showSplash) {
                    SplashScreen(
                        onSplashComplete = { showSplash = false }
                    )
                } else {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavBar(
                navController = navController,
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .statusBarsPadding()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}
