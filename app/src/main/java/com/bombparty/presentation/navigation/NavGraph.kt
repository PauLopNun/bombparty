package com.bombparty.presentation.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
import com.bombparty.utils.Config

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

        composable(Screen.CreateRoom.route) { backStackEntry ->
            // Get ViewModel scoped to this entry (will be shared with child destinations)
            val viewModel: GameViewModel = hiltViewModel(backStackEntry)
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Initialize sound manager
            LaunchedEffect(Unit) {
                viewModel.initSoundManager(context)
            }

            // Auto-navigate to lobby when room is created
            LaunchedEffect(uiState.room?.id) {
                uiState.room?.id?.let { roomId ->
                    navController.navigate(Screen.Lobby.createRoute(roomId))
                }
            }

            // Connect to server on first composition
            LaunchedEffect(Unit) {
                viewModel.connectToServer(Config.SERVER_URL)
            }

            CreateRoomScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    navController.popBackStack()
                },
                onRoomCreated = { _, playerName, avatar, config ->
                    viewModel.createRoom(config, playerName, avatar)
                },
                isLoading = uiState.isLoading,
                isConnected = uiState.isConnected,
                error = uiState.error
            )
        }

        composable(Screen.JoinRoom.route) { backStackEntry ->
            // Get ViewModel scoped to this entry (will be shared with child destinations)
            val viewModel: GameViewModel = hiltViewModel(backStackEntry)
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Initialize sound manager
            LaunchedEffect(Unit) {
                viewModel.initSoundManager(context)
            }

            // Auto-navigate to lobby when room is joined
            LaunchedEffect(uiState.room?.id) {
                uiState.room?.id?.let { roomId ->
                    navController.navigate(Screen.Lobby.createRoute(roomId))
                }
            }

            // Connect to server on first composition
            LaunchedEffect(Unit) {
                viewModel.connectToServer(Config.SERVER_URL)
            }

            JoinRoomScreen(
                onNavigateBack = {
                    viewModel.disconnect()
                    navController.popBackStack()
                },
                onJoinRoom = { roomId, playerName, avatar ->
                    viewModel.joinRoom(roomId, playerName, avatar)
                }
            )
        }

        composable(
            route = Screen.Lobby.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId") ?: return@composable

            // Get ViewModel from the previous entry (CreateRoom or JoinRoom)
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateRoom.route).takeIf {
                    navController.currentBackStack.value.any { entry ->
                        entry.destination.route == Screen.CreateRoom.route
                    }
                } ?: navController.getBackStackEntry(Screen.JoinRoom.route)
            }
            val viewModel: GameViewModel = hiltViewModel(parentEntry)
            val uiState by viewModel.uiState.collectAsState()
            val context = LocalContext.current

            // Auto-navigate to game when game starts
            LaunchedEffect(uiState.gameState) {
                if (uiState.gameState != null) {
                    navController.navigate(Screen.Game.createRoute(roomId))
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

            // Get ViewModel from the previous entry (CreateRoom or JoinRoom via Lobby)
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.CreateRoom.route).takeIf {
                    navController.currentBackStack.value.any { entry ->
                        entry.destination.route == Screen.CreateRoom.route
                    }
                } ?: navController.getBackStackEntry(Screen.JoinRoom.route)
            }
            val viewModel: GameViewModel = hiltViewModel(parentEntry)

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
                modifier = Modifier.fillMaxSize(),
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
