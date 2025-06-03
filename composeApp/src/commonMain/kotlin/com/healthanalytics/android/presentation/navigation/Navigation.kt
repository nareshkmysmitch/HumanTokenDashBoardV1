
package com.healthanalytics.android.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.healthanalytics.android.presentation.screens.BiomarkersScreen
import com.healthanalytics.android.presentation.screens.DashboardScreen
import com.healthanalytics.android.presentation.screens.LoginScreen
import com.healthanalytics.android.presentation.screens.MarketplaceScreen
import com.healthanalytics.android.presentation.screens.RecommendationsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Biomarkers : Screen("biomarkers")
    object Recommendations : Screen("recommendations")
    object Marketplace : Screen("marketplace")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
    accessToken: String?
) {
    NavHost(
        navController = navController,
        startDestination = if (accessToken != null) Screen.Dashboard.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { token ->
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(token = accessToken ?: "")
        }
        
        composable(Screen.Biomarkers.route) {
            BiomarkersScreen(token = accessToken ?: "")
        }
        
        composable(Screen.Recommendations.route) {
            RecommendationsScreen()
        }
        
        composable(Screen.Marketplace.route) {
            MarketplaceScreen(token = accessToken ?: "")
        }
    }
}
