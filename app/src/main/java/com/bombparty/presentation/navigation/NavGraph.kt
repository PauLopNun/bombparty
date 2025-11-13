package com.bombparty.presentation.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.bombparty.presentation.screens.createroom.CreateRoomScreen
import com.bombparty.presentation.screens.game.GameScreen
import com.bombparty.presentation.screens.game.GameViewModel
import com.bombparty.presentation.screens.joinroom.JoinRoomScreen
import com.bombparty.presentation.screens.lobby.LobbyScreen
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
            val viewModel: GameViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Auto-navigate to lobby when room is created
            LaunchedEffect(uiState.room?.id) {
                uiState.room?.id?.let { roomId ->
                    navController.navigate(Screen.Lobby.createRoute(roomId)) {
                        popUpTo(Screen.CreateRoom.route) { inclusive = true }
                    }
                }
            }

            // Connect to server on first composition
            LaunchedEffect(Unit) {
                viewModel.connectToServer("wss://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/game")
            }

            CreateRoomScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    navController.popBackStack()
                },
                onRoomCreated = { _, playerName, config ->
                    viewModel.createRoom(config, playerName)
                }
            )
        }

        composable(Screen.JoinRoom.route) {
            val viewModel: GameViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Auto-navigate to lobby when room is joined
            LaunchedEffect(uiState.room?.id) {
                uiState.room?.id?.let { roomId ->
                    navController.navigate(Screen.Lobby.createRoute(roomId)) {
                        popUpTo(Screen.JoinRoom.route) { inclusive = true }
                    }
                }
            }

            // Connect to server on first composition
            LaunchedEffect(Unit) {
                viewModel.connectToServer("wss://97b87797-ba85-4845-a26d-11759c5ea25f.railway.app/game")
            }

            JoinRoomScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    navController.popBackStack()
                },
                onJoinRoom = { roomId, playerName ->
                    viewModel.joinRoom(roomId, playerName)
                }
            )
        }

        composable(
            route = Screen.Lobby.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable
            val viewModel: GameViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Auto-navigate to game when game starts
            LaunchedEffect(uiState.gameState) {
                if (uiState.gameState != null) {
                    navController.navigate(Screen.Game.createRoute(roomId)) {
                        popUpTo(Screen.Lobby.route) { inclusive = true }
                    }
                }
            }

            LobbyScreen(
                roomId = roomId,
                room = uiState.room,
                currentPlayerId = uiState.currentPlayerId,
                isLoading = uiState.isLoading,
                error = uiState.error,
                onStartGame = {
                    viewModel.startGame(roomId)
                },
                onNavigateBack = {
                    uiState.currentPlayerId?.let { playerId ->
                        viewModel.leaveRoom(roomId, playerId)
                    }
                    navController.popBackStack(Screen.Menu.route, inclusive = false)
                },
                onCopyRoomId = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Room ID", roomId)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "Código copiado", Toast.LENGTH_SHORT).show()
                }
            )
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
            // Simple settings screen placeholder
            Box(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "⚙️ Ajustes",
                        style = MaterialTheme.typography.displaySmall
                    )
                    Text(
                        text = "Próximamente...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(onClick = { navController.popBackStack() }) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}
