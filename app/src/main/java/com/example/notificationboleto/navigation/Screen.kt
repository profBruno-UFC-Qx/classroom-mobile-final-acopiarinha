package com.example.notificationboleto.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddBoleto : Screen("add_boleto")
    object History : Screen("history")
    object Settings : Screen("settings")
}
