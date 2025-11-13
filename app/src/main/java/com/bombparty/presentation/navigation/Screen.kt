package com.bombparty.presentation.navigation

sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object CreateRoom : Screen("create_room")
    object JoinRoom : Screen("join_room")
    object Lobby : Screen("lobby/{roomId}") {
        fun createRoute(roomId: String) = "lobby/$roomId"
    }
    object Game : Screen("game/{roomId}") {
        fun createRoute(roomId: String) = "game/$roomId"
    }
    object Settings : Screen("settings")
}
