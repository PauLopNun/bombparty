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

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bombparty.data.repository.DictionaryRepository
import com.bombparty.domain.model.*
import com.bombparty.network.WebSocketClient
import com.bombparty.network.dto.ServerMessage
import com.bombparty.network.dto.WebSocketMessage
import com.bombparty.network.dto.toDomain
import com.bombparty.utils.SoundManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GameUiState(
    val gameState: GameState? = null,
    val room: GameRoom? = null,
    val currentPlayerId: String? = null,
    val currentWord: String = "",
    val isConnected: Boolean = false,
    val error: String? = null,
    val lastMessage: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class GameViewModel @Inject constructor(
    private val webSocketClient: WebSocketClient,
    private val dictionaryRepository: DictionaryRepository,
    private val sessionManager: com.bombparty.utils.SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var connectionJob: Job? = null
    private var timerJob: Job? = null
    private var soundManager: SoundManager? = null

    fun initSoundManager(context: Context) {
        if (soundManager == null) {
            soundManager = SoundManager(context)
        }
    }

    fun connectToServer(serverUrl: String) {
        // Don't reconnect if already connected or connecting
        if (_uiState.value.isConnected) {
            println("GameViewModel: Already connected, skipping reconnect")
            return
        }

        if (_uiState.value.isLoading) {
            println("GameViewModel: Already connecting, skipping duplicate request")
            return
        }

        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            try {
                println("GameViewModel: Connecting to $serverUrl")
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Connect and collect messages indefinitely (no timeout)
                webSocketClient.connect(
                    serverUrl = serverUrl,
                    onConnected = {
                        println("GameViewModel: Connection established")
                        _uiState.update { it.copy(isConnected = true, isLoading = false) }
                    }
                ).collect { message ->
                    println("GameViewModel: Received message: ${message::class.simpleName}")
                    handleServerMessage(message)
                }
            } catch (e: Exception) {
                println("GameViewModel: Connection error: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isConnected = false,
                        error = "Error de conexión: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun createRoom(config: GameConfig, playerName: String, avatar: String = "😀") {
        viewModelScope.launch {
            try {
                println("GameViewModel: Creating room with player name: $playerName, avatar: $avatar")
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Guardar nombre y avatar en sesión
                sessionManager.playerName = playerName
                sessionManager.playerAvatar = avatar

                webSocketClient.sendMessage(
                    WebSocketMessage.CreateRoom(config, playerName, avatar)
                )
                println("GameViewModel: Create room message sent")
            } catch (e: Exception) {
                println("GameViewModel: Error creating room: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        error = "Error al crear sala: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun joinRoom(roomId: String, playerName: String, avatar: String = "😀") {
        viewModelScope.launch {
            try {
                println("GameViewModel: Joining room $roomId with player name: $playerName, avatar: $avatar")

                // Guardar en sesión
                sessionManager.playerName = playerName
                sessionManager.playerAvatar = avatar

                webSocketClient.sendMessage(
                    WebSocketMessage.JoinRoom(roomId, playerName, avatar)
                )
                println("GameViewModel: Join room message sent")
            } catch (e: Exception) {
                println("GameViewModel: Error joining room: ${e.message}")
                e.printStackTrace()
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

    fun restartGame(roomId: String) {
        viewModelScope.launch {
            try {
                webSocketClient.sendMessage(WebSocketMessage.RestartGame(roomId))
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error restarting game: ${e.message}") }
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
                println("GameViewModel: Room created with ID: ${message.room.id}")

                // Convert DTO to domain model
                val roomDomain = message.room.toDomain()

                // Guardar sesión
                sessionManager.roomId = roomDomain.id
                sessionManager.playerId = message.playerId
                sessionManager.isInGame = true

                _uiState.update {
                    it.copy(
                        room = roomDomain,
                        currentPlayerId = message.playerId,
                        isConnected = true,
                        isLoading = false,
                        lastMessage = "Sala creada: ${roomDomain.id}"
                    )
                }
            }

            is ServerMessage.RoomJoined -> {
                println("=== GameViewModel: RoomJoined Message Received ===")
                println("Room ID: ${message.room.id}")
                println("Player ID: ${message.playerId}")
                println("DTO Room has ${message.room.players.size} players:")
                message.room.players.forEach { player ->
                    println("  - DTO Player: id=${player.id}, name=${player.name}, avatar=${player.avatar}, lives=${player.lives}")
                }
                println("Max players: ${message.room.config.maxPlayers}")

                // Convert DTO to domain model
                println("=== Converting DTO to Domain Model ===")
                val roomDomain = message.room.toDomain()
                println("Domain room has ${roomDomain.players.size} players:")
                roomDomain.players.forEach { player ->
                    println("  - Domain Player: id=${player.id}, name=${player.name}, avatar=${player.avatar}, lives=${player.lives}")
                }

                // Guardar sesión
                println("=== Saving Session ===")
                sessionManager.roomId = roomDomain.id
                sessionManager.playerId = message.playerId
                sessionManager.isInGame = true
                println("Saved: roomId=${sessionManager.roomId}, playerId=${sessionManager.playerId}")

                println("=== Updating UI State ===")
                _uiState.update {
                    it.copy(
                        room = roomDomain,
                        currentPlayerId = message.playerId,
                        isConnected = true,
                        isLoading = false,
                        lastMessage = "Joined room successfully"
                    )
                }

                println("=== UI State After Update ===")
                println("Room: ${_uiState.value.room?.id}")
                println("Players: ${_uiState.value.room?.players?.size}")
                _uiState.value.room?.players?.forEach { player ->
                    println("  - Player: id=${player.id}, name=${player.name}, avatar=${player.avatar}")
                }
                println("Current Player ID: ${_uiState.value.currentPlayerId}")
                println("Is Connected: ${_uiState.value.isConnected}")
                println("Is Loading: ${_uiState.value.isLoading}")
                println("===========================================")
            }

            is ServerMessage.GameStarted -> {
                soundManager?.playSound(SoundManager.SoundType.GAME_START)
                val gameStateDomain = message.gameState.toDomain()
                _uiState.update {
                    it.copy(
                        gameState = gameStateDomain,
                        lastMessage = "Game started!"
                    )
                }
                startBombTimer()
            }

            is ServerMessage.GameStateUpdate -> {
                val gameStateDomain = message.gameState.toDomain()
                _uiState.update {
                    it.copy(gameState = gameStateDomain)
                }
            }

            is ServerMessage.WordAccepted -> {
                soundManager?.playSound(SoundManager.SoundType.WORD_CORRECT)
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
                soundManager?.playSound(SoundManager.SoundType.WORD_INCORRECT)
                _uiState.update {
                    it.copy(
                        lastMessage = "Word rejected: ${message.reason}"
                    )
                }
            }

            is ServerMessage.BombExplodedEvent -> {
                soundManager?.stopSound(SoundManager.SoundType.BOMB_TICK)
                soundManager?.playSound(SoundManager.SoundType.BOMB_EXPLODE)
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
                soundManager?.stopSound(SoundManager.SoundType.BOMB_TICK)
                soundManager?.playSound(SoundManager.SoundType.VICTORY)
                _uiState.update {
                    it.copy(
                        lastMessage = "${message.winnerName} wins!"
                    )
                }
            }

            is ServerMessage.NewSyllable -> {
                soundManager?.playSoundLoop(SoundManager.SoundType.BOMB_TICK, 0.5f)
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
                val playerDomain = message.player.toDomain()
                println("GameViewModel: PlayerJoined event - ${playerDomain.name}")
                _uiState.update { state ->
                    val updatedRoom = state.room?.addPlayer(playerDomain)
                    println("GameViewModel: Updated room with ${updatedRoom?.players?.size} players")
                    state.copy(
                        room = updatedRoom ?: state.room,
                        lastMessage = "${playerDomain.name} joined the room"
                    )
                }
            }

            is ServerMessage.PlayerLeft -> {
                _uiState.update { state ->
                    val updatedRoom = state.room?.removePlayer(message.playerId)
                    state.copy(
                        room = updatedRoom ?: state.room,
                        lastMessage = "A player left the room"
                    )
                }
            }

            is ServerMessage.ConfigUpdated -> {
                val configDomain = message.config.toDomain()
                _uiState.update { state ->
                    state.copy(
                        gameState = state.gameState?.copy(config = configDomain)
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
            soundManager?.stopAllSounds()
            webSocketClient.disconnect()

            // Limpiar sesión
            sessionManager.clearSession()

            _uiState.update { GameUiState() }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
        soundManager?.release()
    }
}
