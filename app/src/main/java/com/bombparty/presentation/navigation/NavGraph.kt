package com.bombparty.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bombparty.presentation.screens.game.GameScreen
import com.bombparty.presentation.screens.game.GameViewModel
import com.bombparty.presentation.screens.menu.MenuScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Menu.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Menu.route) {
            MenuScreen(
                onCreateRoom = {
                    navController.navigate(Screen.CreateRoom.route)
                },
                onJoinRoom = {
                    navController.navigate(Screen.JoinRoom.route)
                },
                onSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.CreateRoom.route) {
            // CreateRoomScreen will be implemented
        }

        composable(Screen.JoinRoom.route) {
            // JoinRoomScreen will be implemented
        }

        composable(
            route = Screen.Lobby.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            // LobbyScreen will be implemented
        }

        composable(
            route = Screen.Game.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val viewModel: GameViewModel = hiltViewModel()
            GameScreen(
                viewModel = viewModel,
                roomId = roomId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            // SettingsScreen will be implemented
        }
    }
}
