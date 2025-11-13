package com.bombparty.presentation.screens.game

/**
 * CONFIGURACIÓN DEL SERVIDOR
 *
 * Para conectarte al servidor, necesitas especificar la URL correcta:
 *
 * - Para EMULADOR de Android:
 *   "ws://10.0.2.2:8080/game"
 *
 * - Para DISPOSITIVO FÍSICO (celular/tablet en la misma red WiFi):
 *   "ws://TU_IP_LOCAL:8080/game"
 *   Ejemplo: "ws://192.168.1.100:8080/game"
 *
 *   Para encontrar tu IP:
 *   - Windows: abre CMD y escribe "ipconfig" (busca IPv4)
 *   - Mac: abre Terminal y escribe "ifconfig" (busca inet)
 *
 * - Para SERVIDOR EN INTERNET:
 *   "ws://tu-servidor.com:8080/game"
 *
 * La URL se configura cuando llamas a connectToServer() desde GameScreen.kt
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bombparty.data.repository.DictionaryRepository
import com.bombparty.domain.model.*
import com.bombparty.network.WebSocketClient
import com.bombparty.network.dto.ServerMessage
import com.bombparty.network.dto.WebSocketMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val gameState: GameState? = null,
    val currentWord: String = "",
    val isConnected: Boolean = false,
    val error: String? = null,
    val lastMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val dictionaryRepository: DictionaryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var connectionJob: Job? = null
    private var timerJob: Job? = null

    fun connectToServer(serverUrl: String) {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                webSocketClient.connect(serverUrl).collect { message ->
                    handleServerMessage(message)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        error = "Connection error: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createRoom(config: GameConfig, playerName: String) {
        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(
                    WebSocketMessage.CreateRoom(config, playerName)
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error creating room: ${e.message}") }
            }
        }
    }

    fun joinRoom(roomId: String, playerName: String) {
        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(
                    WebSocketMessage.JoinRoom(roomId, playerName)
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error joining room: ${e.message}") }
            }
        }
    }

    fun startGame(roomId: String) {
        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(WebSocketMessage.StartGame(roomId))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error starting game: ${e.message}") }
            }
        }
    }

    fun submitWord(roomId: String, playerId: String) {
        val word = _uiState.value.currentWord.trim()
        if (word.isEmpty()) return

        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(
                    WebSocketMessage.SubmitWord(roomId, playerId, word)
                )
                _uiState.update { it.copy(currentWord = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error submitting word: ${e.message}") }
            }
        }
    }

    fun updateWord(word: String) {
        _uiState.update { it.copy(currentWord = word) }
    }

    fun leaveRoom(roomId: String, playerId: String) {
        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(
                    WebSocketMessage.LeaveRoom(roomId, playerId)
                )
                disconnect()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error leaving room: ${e.message}") }
            }
        }
    }

    private fun handleServerMessage(message: ServerMessage) {
        when (message) {
            is ServerMessage.RoomCreated -> {
                _uiState.update {
                    it.copy(
                        isConnected = true,
                        isLoading = false,
                        lastMessage = "Room created: ${message.room.id}"
                    )
                }
            }

            is ServerMessage.RoomJoined -> {
                _uiState.update {
                    it.copy(
                        isConnected = true,
                        isLoading = false,
                        lastMessage = "Joined room successfully"
                    )
                }
            }

            is ServerMessage.GameStarted -> {
                _uiState.update {
                    it.copy(
                        gameState = message.gameState,
                        lastMessage = "Game started!"
                    )
                }
                startBombTimer()
            }

            is ServerMessage.GameStateUpdate -> {
                _uiState.update {
                    it.copy(gameState = message.gameState)
                }
            }

            is ServerMessage.WordAccepted -> {
                _uiState.update {
                    it.copy(
                        lastMessage = if (message.gainedLife) {
                            "Word accepted! +1 life"
                        } else {
                            "Word accepted!"
                        }
                    )
                }
            }

            is ServerMessage.WordRejected -> {
                _uiState.update {
                    it.copy(
                        lastMessage = "Word rejected: ${message.reason}"
                    )
                }
            }

            is ServerMessage.BombExplodedEvent -> {
                _uiState.update {
                    it.copy(
                        lastMessage = "${message.playerName} lost a life! ${message.livesRemaining} remaining"
                    )
                }
            }

            is ServerMessage.PlayerEliminated -> {
                _uiState.update {
                    it.copy(
                        lastMessage = "${message.playerName} has been eliminated!"
                    )
                }
            }

            is ServerMessage.GameFinished -> {
                timerJob?.cancel()
                _uiState.update {
                    it.copy(
                        lastMessage = "${message.winnerName} wins!"
                    )
                }
            }

            is ServerMessage.NewSyllable -> {
                _uiState.update { state ->
                    val updatedBombState = state.gameState?.bombState?.copy(
                        currentSyllable = message.syllable,
                        maxTime = message.bombTime,
                        timeRemaining = message.bombTime
                    ) ?: BombState(
                        currentSyllable = message.syllable,
                        timeRemaining = message.bombTime,
                        maxTime = message.bombTime,
                        syllableTurnsRemaining = state.gameState?.config?.maxSyllableLifespan ?: 2
                    )

                    state.copy(
                        gameState = state.gameState?.copy(bombState = updatedBombState)
                    )
                }
                startBombTimer()
            }

            is ServerMessage.BombTimerUpdate -> {
                _uiState.update { state ->
                    val updatedBombState = state.gameState?.bombState?.copy(
                        timeRemaining = message.timeRemaining
                    )
                    state.copy(
                        gameState = state.gameState?.copy(bombState = updatedBombState)
                    )
                }
            }

            is ServerMessage.Error -> {
                _uiState.update {
                    it.copy(error = message.message)
                }
            }

            is ServerMessage.PlayerJoined -> {
                _uiState.update {
                    it.copy(lastMessage = "${message.player.name} joined the room")
                }
            }

            is ServerMessage.PlayerLeft -> {
                _uiState.update {
                    it.copy(lastMessage = "A player left the room")
                }
            }

            is ServerMessage.ConfigUpdated -> {
                _uiState.update { state ->
                    state.copy(
                        gameState = state.gameState?.copy(config = message.config)
                    )
                }
            }
        }
    }

    private fun startBombTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.gameState?.bombState?.timeRemaining ?: 0f > 0f) {
                delay(100) // Update every 100ms for smooth animation
                _uiState.update { state ->
                    val bombState = state.gameState?.bombState
                    if (bombState != null && bombState.timeRemaining > 0) {
                        val newTime = (bombState.timeRemaining - 0.1f).coerceAtLeast(0f)
                        state.copy(
                            gameState = state.gameState.copy(
                                bombState = bombState.copy(timeRemaining = newTime)
                            )
                        )
                    } else {
                        state
                    }
                }
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            timerJob?.cancel()
            connectionJob?.cancel()
            webSocketClient.disconnect()
            _uiState.update { GameUiState() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
