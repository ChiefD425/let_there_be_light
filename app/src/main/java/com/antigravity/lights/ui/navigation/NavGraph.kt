package com.antigravity.lights.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.material3.Text
import com.antigravity.lights.ui.dashboard.DashboardScreen

@Composable
fun LightNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToPattern = { deviceId ->
                    navController.navigate(Screen.PatternCreator.createRoute(deviceId))
                }
            )
        }
        composable(Screen.PatternCreator.route) { 
            // DeviceId is already in ViewModel via SavedStateHandle
            com.antigravity.lights.ui.pattern.PatternCreatorScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Scheduler.route) {
            com.antigravity.lights.ui.scheduler.SchedulerScreen()
        }
        composable(Screen.Settings.route) {
            Text(text = "Settings")
        }
    }
}
