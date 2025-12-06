package com.antigravity.lights.ui.navigation

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object PatternCreator : Screen("pattern_creator/{deviceId}") {
        fun createRoute(deviceId: String) = "pattern_creator/$deviceId"
    }
    object Scheduler : Screen("scheduler")
    object Settings : Screen("settings")
}
