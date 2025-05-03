package com.example.cuestionarios.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Questionary : Screen("questionary")
    object User : Screen("user")
}
